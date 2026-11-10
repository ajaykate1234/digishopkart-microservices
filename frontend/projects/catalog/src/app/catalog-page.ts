import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  CATEGORIES,
  CatalogFacets,
  CartStore,
  discountPercent,
  errorMessage,
  imageUrl,
  primaryImage,
  Product,
  SessionStore,
  ShopService,
} from '@digi/shared';

@Component({
  selector: 'app-catalog-page',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './catalog-page.html',
})
export class CatalogPage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cart = inject(CartStore);
  private readonly session = inject(SessionStore);

  protected readonly products = signal<Product[]>([]);
  protected readonly facets = signal<CatalogFacets | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly categories = signal<string[]>([]);
  protected readonly brands = signal<string[]>([]);
  protected readonly minPrice = signal<number | null>(null);
  protected readonly maxPrice = signal<number | null>(null);
  protected readonly query = signal('');
  protected readonly imageUrl = imageUrl;
  protected readonly primaryImage = primaryImage;
  protected readonly discountPercent = discountPercent;
  protected readonly categoryOptions = CATEGORIES;
  protected readonly canEdit = computed(() => {
    const role = this.session.user()?.role;
    return role === 'ADMIN' || role === 'STAFF';
  });
  protected readonly saving = signal(false);
  protected readonly form = new FormGroup({
    productName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    brand: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    productPrice: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    mrp: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    highlight: new FormControl('', { nonNullable: true }),
    productCategory: new FormControl('electronics', { nonNullable: true, validators: [Validators.required] }),
    variantName: new FormControl('Colour', { nonNullable: true, validators: [Validators.required] }),
    variantValue: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    this.shop.facets().subscribe({
      next: (facets) => this.facets.set(facets),
      error: () => this.facets.set(null),
    });
    this.route.queryParamMap.subscribe((params) => {
      this.query.set(params.get('q') ?? '');
      this.categories.set(params.getAll('category'));
      this.brands.set(params.getAll('brand'));
      this.minPrice.set(params.get('minPrice') ? Number(params.get('minPrice')) : null);
      this.maxPrice.set(params.get('maxPrice') ? Number(params.get('maxPrice')) : null);
      this.load();
    });
  }

  protected load(): void {
    this.loading.set(true);
    this.shop.products({
      q: this.query(),
      category: this.categories(),
      brand: this.brands(),
      minPrice: this.minPrice(),
      maxPrice: this.maxPrice(),
    }).subscribe({
      next: (rows) => {
        this.products.set(rows);
        this.loading.set(false);
        this.error.set(null);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.error.set(errorMessage(error, 'Could not load the catalog.'));
      },
    });
  }

  protected toggle(list: string[], value: string, key: 'category' | 'brand'): void {
    const next = list.includes(value) ? list.filter((item) => item !== value) : [...list, value];
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { [key]: next.length ? next : null },
      queryParamsHandling: 'merge',
    });
  }

  protected applyPrice(min: string, max: string): void {
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        minPrice: min === '' ? null : Number(min),
        maxPrice: max === '' ? null : Number(max),
      },
      queryParamsHandling: 'merge',
    });
  }

  protected clear(): void {
    void this.router.navigate(['/catalog']);
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.saving.set(true);
    this.shop.createProduct({
      productName: value.productName,
      brand: value.brand,
      productPrice: value.productPrice,
      mrp: value.mrp,
      highlight: value.highlight,
      productCategory: value.productCategory,
      productStatus: 'active',
      variants: [{ name: value.variantName, value: value.variantValue, status: 'active' }],
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.notice.set('Product saved. Open it to upload a picture.');
        this.form.reset({
          productName: '',
          brand: '',
          productPrice: 0,
          mrp: 0,
          highlight: '',
          productCategory: 'electronics',
          variantName: 'Colour',
          variantValue: '',
        });
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not save the product.'));
      },
    });
  }

  protected add(product: Product): void {
    const image = primaryImage(product);
    this.cart.add({
      productId: product.id,
      productName: product.productName,
      brand: product.brand,
      price: product.productPrice,
      imageId: image?.id ?? null,
    });
    this.notice.set(`${product.productName} added to the bag.`);
  }
}
