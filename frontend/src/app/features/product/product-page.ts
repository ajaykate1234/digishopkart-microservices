import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { errorMessage } from '../../core/api-error';
import { CATEGORIES, Product } from '../../core/models';
import { SessionStore } from '../../core/session.store';
import { ShopService } from '../shop/shop.service';

@Component({
  selector: 'app-product-page',
  imports: [ReactiveFormsModule],
  templateUrl: './product-page.html',
})
export class ProductPage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly session = inject(SessionStore);

  protected readonly categories = CATEGORIES;
  protected readonly products = signal<Product[]>([]);
  protected readonly query = signal('');
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly canEdit = computed(() => {
    const role = this.session.user()?.role;
    return role === 'ADMIN' || role === 'STAFF';
  });
  protected readonly visible = computed(() => {
    const query = this.query().trim().toLowerCase();
    return this.products().filter((product) => {
      if (!query) {
        return true;
      }
      return `${product.productName} ${product.brand} ${product.productCategory}`.toLowerCase().includes(query);
    });
  });
  protected readonly form = new FormGroup({
    productName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    brand: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    productPrice: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    productCategory: new FormControl('electronics', { nonNullable: true, validators: [Validators.required] }),
    variantName: new FormControl('Colour', { nonNullable: true, validators: [Validators.required] }),
    variantValue: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.shop.products().subscribe({
      next: (rows) => {
        this.products.set(rows);
        this.error.set(null);
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load products.')),
    });
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
      productCategory: value.productCategory,
      productStatus: 'active',
      variants: [{ name: value.variantName, value: value.variantValue, status: 'active' }],
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.notice.set('Product saved in the product service.');
        this.form.reset({ productName: '', brand: '', productPrice: 0, productCategory: 'electronics', variantName: 'Colour', variantValue: '' });
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not save the product.'));
      },
    });
  }
}
