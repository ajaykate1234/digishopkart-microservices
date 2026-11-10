import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Customer, errorMessage, ShopService } from '@digi/shared';

@Component({
  selector: 'app-profile-page',
  imports: [ReactiveFormsModule],
  templateUrl: './profile-page.html',
})
export class ProfilePage implements OnInit {
  private readonly shop = inject(ShopService);
  protected readonly customer = signal<Customer | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly form = new FormGroup({
    addressType: new FormControl('shipping', { nonNullable: true }),
    customerFullName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    country: new FormControl('India', { nonNullable: true, validators: [Validators.required] }),
    state: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    town: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    area: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    houseOrBuilding: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    landmark: new FormControl('', { nonNullable: true }),
    pinCode: new FormControl(411001, { nonNullable: true, validators: [Validators.required] }),
    mobile: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    this.shop.me().subscribe({
      next: (customer) => {
        this.customer.set(customer);
        if (!this.form.controls.customerFullName.value) {
          this.form.patchValue({ customerFullName: `${customer.firstName} ${customer.lastName}`, mobile: customer.mobile });
        }
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'No customer profile for this login.')),
    });
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.shop.addAddress(this.form.getRawValue()).subscribe({
      next: () => {
        this.saving.set(false);
        this.notice.set('Address saved.');
        this.shop.me().subscribe((customer) => this.customer.set(customer));
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not save the address.'));
      },
    });
  }
}
