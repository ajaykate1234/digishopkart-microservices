import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Discount, errorMessage, SessionStore, ShopService } from '@digi/shared';

@Component({
  selector: 'app-offers-page',
  imports: [ReactiveFormsModule],
  templateUrl: './offers-page.html',
})
export class OffersPage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly session = inject(SessionStore);
  protected readonly rows = signal<Discount[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly isAdmin = () => this.session.user()?.role === 'ADMIN';
  protected readonly form = new FormGroup({
    couponName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    discountType: new FormControl('FLAT', { nonNullable: true }),
    couponValue: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
  });

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.shop.discounts().subscribe({
      next: (rows) => this.rows.set(rows),
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load offers.')),
    });
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.shop.createDiscount(this.form.getRawValue()).subscribe({
      next: () => {
        this.saving.set(false);
        this.notice.set('Offer saved.');
        this.form.reset({ couponName: '', discountType: 'FLAT', couponValue: 0 });
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not save the offer.'));
      },
    });
  }
}
