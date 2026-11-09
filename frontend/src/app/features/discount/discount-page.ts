import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { errorMessage } from '../../core/api-error';
import { Discount } from '../../core/models';
import { SessionStore } from '../../core/session.store';
import { ShopService } from '../shop/shop.service';

@Component({
  selector: 'app-discount-page',
  imports: [ReactiveFormsModule],
  templateUrl: './discount-page.html',
})
export class DiscountPage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly session = inject(SessionStore);

  protected readonly rows = signal<Discount[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly isAdmin = computed(() => this.session.user()?.role === 'ADMIN');
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
      next: (rows) => {
        this.rows.set(rows);
        this.error.set(null);
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not load discounts.')),
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
        this.notice.set('Discount saved.');
        this.form.reset({ couponName: '', discountType: 'FLAT', couponValue: 0 });
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Could not save the discount.'));
      },
    });
  }
}
