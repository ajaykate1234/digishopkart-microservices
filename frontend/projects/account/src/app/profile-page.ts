import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Address, Customer, errorMessage, ShopService } from '@digi/shared';

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
  protected readonly editingAddressId = signal<number | null>(null);
  protected readonly profile = new FormGroup({
    firstName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    lastName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    mobile: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });
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
    this.refresh();
  }

  protected saveProfile(): void {
    const current = this.customer();
    if (!current || this.profile.invalid) {
      this.profile.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.shop.updateCustomer(current.id, {
      ...this.profile.getRawValue(),
      customerStatus: current.customerStatus,
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.notice.set('Profile updated.');
        this.refresh();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not update the profile.'));
      },
    });
  }

  protected editAddress(address: Address): void {
    this.editingAddressId.set(address.id);
    this.form.reset({
      addressType: address.addressType,
      customerFullName: address.customerFullName,
      country: address.country,
      state: address.state,
      town: address.town,
      area: address.area,
      houseOrBuilding: address.houseOrBuilding,
      landmark: address.landmark ?? '',
      pinCode: address.pinCode,
      mobile: address.mobile,
    });
  }

  protected cancelAddress(): void {
    this.editingAddressId.set(null);
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const body = this.form.getRawValue();
    const addressId = this.editingAddressId();
    this.saving.set(true);
    const request = addressId == null ? this.shop.addAddress(body) : this.shop.updateAddress(addressId, body);
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.notice.set(addressId == null ? 'Address saved.' : 'Address updated.');
        this.editingAddressId.set(null);
        this.refresh();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not save the address.'));
      },
    });
  }

  protected removeAddress(addressId: number): void {
    this.shop.deleteAddress(addressId).subscribe({
      next: () => {
        this.notice.set('Address removed.');
        if (this.editingAddressId() === addressId) {
          this.editingAddressId.set(null);
        }
        this.refresh();
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not delete the address.')),
    });
  }

  private refresh(): void {
    this.shop.me().subscribe({
      next: (customer) => {
        this.customer.set(customer);
        this.profile.reset({
          firstName: customer.firstName,
          lastName: customer.lastName,
          email: customer.email,
          mobile: customer.mobile,
        });
        this.error.set(null);
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'No customer profile for this login.')),
    });
  }
}
