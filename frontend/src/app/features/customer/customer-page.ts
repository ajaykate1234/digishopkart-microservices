import { Component, inject, OnInit, signal } from '@angular/core';
import { errorMessage } from '../../core/api-error';
import { Customer } from '../../core/models';
import { ShopService } from '../shop/shop.service';

@Component({
  selector: 'app-customer-page',
  templateUrl: './customer-page.html',
})
export class CustomerPage implements OnInit {
  private readonly shop = inject(ShopService);

  protected readonly rows = signal<Customer[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.shop.customers().subscribe({
      next: (rows) => {
        this.rows.set(rows);
        this.error.set(null);
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load customers.')),
    });
  }

  protected toggle(customer: Customer): void {
    const next = customer.customerStatus === 'active' ? 'inactive' : 'active';
    this.shop.updateCustomer(customer.id, {
      firstName: customer.firstName,
      lastName: customer.lastName,
      email: customer.email,
      mobile: customer.mobile,
      customerStatus: next,
    }).subscribe({
      next: () => {
        this.notice.set(`${customer.email} is now ${next}.`);
        this.load();
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not update the customer.')),
    });
  }
}
