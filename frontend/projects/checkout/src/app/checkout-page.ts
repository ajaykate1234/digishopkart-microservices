import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CartStore, Discount, errorMessage, imageUrl, ORDER_STATUSES, SessionStore, ShopOrder, ShopService } from '@digi/shared';

@Component({
  selector: 'app-checkout-page',
  imports: [ReactiveFormsModule, FormsModule],
  templateUrl: './checkout-page.html',
})
export class CheckoutPage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly session = inject(SessionStore);
  protected readonly cart = inject(CartStore);
  protected readonly statuses = ORDER_STATUSES;
  protected readonly discounts = signal<Discount[]>([]);
  protected readonly orders = signal<ShopOrder[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly imageUrl = imageUrl;
  protected readonly isAdmin = () => this.session.user()?.role === 'ADMIN';
  protected readonly form = new FormGroup({
    customerId: new FormControl<number | null>(null, { validators: [Validators.required] }),
    discountId: new FormControl<number | null>(null),
  });

  ngOnInit(): void {
    this.shop.discounts().subscribe({
      next: (rows) => this.discounts.set(rows),
      error: () => this.discounts.set([]),
    });
    if (this.session.user()?.role === 'CUSTOMER') {
      this.shop.me().subscribe({
        next: (customer) => this.form.controls.customerId.setValue(customer.id),
        error: () => this.error.set('This login has no customer profile.'),
      });
    }
    this.loadOrders();
  }

  protected total(): number {
    return this.cart.lines().reduce((sum, line) => sum + line.price * line.quantity, 0);
  }

  protected loadOrders(): void {
    this.shop.orders().subscribe({
      next: (rows) => this.orders.set(rows),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load orders.')),
    });
  }

  protected place(): void {
    const lines = this.cart.lines();
    if (this.form.invalid || lines.length === 0) {
      this.error.set('Choose a customer and add at least one product.');
      return;
    }
    const productIds = lines.flatMap((line) => Array.from({ length: line.quantity }, () => line.productId));
    this.saving.set(true);
    this.shop.placeOrder({
      customerId: this.form.controls.customerId.value,
      productIds,
      discountId: this.form.controls.discountId.value || null,
    }).subscribe({
      next: (order) => {
        this.saving.set(false);
        this.notice.set(`Order ${order.orderId} confirmed at ₹${order.totalPrice}.`);
        this.cart.clear();
        this.loadOrders();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not place the order.'));
      },
    });
  }

  protected changeStatus(order: ShopOrder, status: string): void {
    this.shop.updateOrderStatus(order.orderId, status).subscribe({
      next: () => this.loadOrders(),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not update the status.')),
    });
  }
}
