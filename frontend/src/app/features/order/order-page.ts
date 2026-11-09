import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { errorMessage } from '../../core/api-error';
import { Discount, ORDER_STATUSES, Product, ShopOrder } from '../../core/models';
import { SessionStore } from '../../core/session.store';
import { ShopService } from '../shop/shop.service';

@Component({
  selector: 'app-order-page',
  imports: [ReactiveFormsModule, FormsModule],
  templateUrl: './order-page.html',
})
export class OrderPage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly session = inject(SessionStore);

  protected readonly statuses = ORDER_STATUSES;
  protected readonly products = signal<Product[]>([]);
  protected readonly discounts = signal<Discount[]>([]);
  protected readonly orders = signal<ShopOrder[]>([]);
  protected readonly picked = signal<number[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly isAdmin = computed(() => this.session.user()?.role === 'ADMIN');
  protected readonly form = new FormGroup({
    customerId: new FormControl<number | null>(null, { validators: [Validators.required] }),
    discountId: new FormControl<number | null>(null),
  });

  ngOnInit(): void {
    this.shop.products().subscribe({
      next: (rows) => this.products.set(rows.filter((row) => row.productStatus === 'active')),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load products.')),
    });
    this.shop.discounts().subscribe({
      next: (rows) => this.discounts.set(rows),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load discounts.')),
    });
    if (this.session.user()?.role === 'CUSTOMER') {
      this.shop.me().subscribe({
        next: (customer) => this.form.controls.customerId.setValue(customer.id),
        error: () => this.error.set('This login has no customer profile, so it cannot place an order.'),
      });
    }
    this.load();
  }

  protected load(): void {
    this.shop.orders().subscribe({
      next: (rows) => this.orders.set(rows),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load orders.')),
    });
  }

  protected toggle(productId: number): void {
    const current = this.picked();
    this.picked.set(current.includes(productId) ? current.filter((id) => id !== productId) : [...current, productId]);
  }

  protected place(): void {
    if (this.form.invalid || this.picked().length === 0) {
      this.form.markAllAsTouched();
      this.error.set('Choose a customer and at least one product.');
      return;
    }
    const value = this.form.getRawValue();
    this.saving.set(true);
    this.shop.placeOrder({
      customerId: value.customerId,
      productIds: this.picked(),
      discountId: value.discountId || null,
    }).subscribe({
      next: (order) => {
        this.saving.set(false);
        this.notice.set(`Order ${order.orderId} confirmed at ${order.totalPrice}.`);
        this.picked.set([]);
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not place the order.'));
      },
    });
  }

  protected changeStatus(order: ShopOrder, status: string): void {
    this.shop.updateOrderStatus(order.orderId, status).subscribe({
      next: () => this.load(),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not update the status.')),
    });
  }
}
