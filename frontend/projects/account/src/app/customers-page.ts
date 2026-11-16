import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Customer, errorMessage, ShopService } from '@digi/shared';

@Component({
  selector: 'app-customers-page',
  imports: [ReactiveFormsModule],
  templateUrl: './customers-page.html',
})
export class CustomersPage implements OnInit {
  private readonly shop = inject(ShopService);
  protected readonly rows = signal<Customer[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly editingId = signal<number | null>(null);
  protected readonly form = new FormGroup({
    firstName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    lastName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    mobile: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    customerStatus: new FormControl('active', { nonNullable: true }),
  });

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.shop.customers().subscribe({
      next: (rows) => this.rows.set(rows),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load customers.')),
    });
  }

  protected edit(customer: Customer): void {
    this.editingId.set(customer.id);
    this.form.reset({
      firstName: customer.firstName,
      lastName: customer.lastName,
      email: customer.email,
      mobile: customer.mobile,
      customerStatus: customer.customerStatus,
    });
  }

  protected save(): void {
    const id = this.editingId();
    if (id == null || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.shop.updateCustomer(id, this.form.getRawValue()).subscribe({
      next: () => {
        this.notice.set('Customer updated.');
        this.editingId.set(null);
        this.load();
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not update the customer.')),
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
