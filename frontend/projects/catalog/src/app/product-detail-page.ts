import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  CATEGORIES,
  CartStore,
  discountPercent,
  errorMessage,
  imageUrl,
  Product,
  SessionStore,
  ShopService,
  Variant,
} from '@digi/shared';

@Component({
  selector: 'app-product-detail-page',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './product-detail-page.html',
})
export class ProductDetailPage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cart = inject(CartStore);
  private readonly session = inject(SessionStore);

  protected readonly product = signal<Product | null>(null);
  protected readonly selectedImage = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly imageUrl = imageUrl;
  protected readonly discountPercent = discountPercent;
  protected readonly categoryOptions = CATEGORIES;
  protected readonly canEdit = () => {
    const role = this.session.user()?.role;
    return role === 'ADMIN' || role === 'STAFF';
  };
  protected readonly form = new FormGroup({
    productName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    brand: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    productPrice: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    mrp: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    highlight: new FormControl('', { nonNullable: true }),
    productCategory: new FormControl('electronics', { nonNullable: true, validators: [Validators.required] }),
    productStatus: new FormControl('active', { nonNullable: true, validators: [Validators.required] }),
  });
  protected readonly variantForm = new FormGroup({
    name: new FormControl('Colour', { nonNullable: true, validators: [Validators.required] }),
    value: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => this.load(Number(params.get('productId'))));
  }

  protected add(): void {
    const product = this.product();
    if (!product) {
      return;
    }
    this.cart.add({
      productId: product.id,
      productName: product.productName,
      brand: product.brand,
      price: product.productPrice,
      imageId: this.selectedImage(),
    });
    this.notice.set('Added to the bag. Open the bag to apply a coupon and place the order.');
  }

  protected save(): void {
    const product = this.product();
    if (!product || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.shop.updateProduct(product.id, this.form.getRawValue()).subscribe({
      next: (fresh) => {
        this.saving.set(false);
        this.notice.set('Product updated.');
        this.apply(fresh);
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not update the product.'));
      },
    });
  }

  protected remove(): void {
    const product = this.product();
    if (!product) {
      return;
    }
    this.shop.deleteProduct(product.id).subscribe({
      next: () => void this.router.navigate(['/catalog']),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not delete the product.')),
    });
  }

  protected upload(event: Event): void {
    const product = this.product();
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!product || !file) {
      return;
    }
    this.shop.uploadImage(product.id, file).subscribe({
      next: () => {
        input.value = '';
        this.notice.set('Picture saved.');
        this.load(product.id);
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not upload the picture.')),
    });
  }

  protected removeImage(imageId: number): void {
    const product = this.product();
    if (!product) {
      return;
    }
    this.shop.deleteImage(product.id, imageId).subscribe({
      next: () => this.load(product.id),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not delete the picture.')),
    });
  }

  protected addVariant(): void {
    const product = this.product();
    if (!product || this.variantForm.invalid) {
      this.variantForm.markAllAsTouched();
      return;
    }
    const value = this.variantForm.getRawValue();
    this.shop.addVariant(product.id, { name: value.name, value: value.value, status: 'active' }).subscribe({
      next: () => {
        this.variantForm.reset({ name: 'Colour', value: '' });
        this.load(product.id);
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not add the variant.')),
    });
  }

  protected saveVariant(variant: Variant, name: string, value: string): void {
    const product = this.product();
    if (!product || !name.trim() || !value.trim()) {
      return;
    }
    this.shop.updateVariant(variant.id, { name: name.trim(), value: value.trim(), status: variant.status }).subscribe({
      next: () => {
        this.notice.set('Variant updated.');
        this.load(product.id);
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not update the variant.')),
    });
  }

  protected removeVariant(variantId: number): void {
    const product = this.product();
    if (!product) {
      return;
    }
    this.shop.deleteVariant(variantId).subscribe({
      next: () => this.load(product.id),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not delete the variant.')),
    });
  }

  private load(id: number): void {
    this.shop.product(id).subscribe({
      next: (product) => this.apply(product),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Product was not found.')),
    });
  }

  private apply(product: Product): void {
    this.product.set(product);
    this.selectedImage.set(product.images.find((image) => image.primaryImage)?.id ?? product.images[0]?.id ?? null);
    this.form.reset({
      productName: product.productName,
      brand: product.brand,
      productPrice: product.productPrice,
      mrp: product.mrp,
      highlight: product.highlight ?? '',
      productCategory: product.productCategory,
      productStatus: product.productStatus,
    });
    this.error.set(null);
  }
}
