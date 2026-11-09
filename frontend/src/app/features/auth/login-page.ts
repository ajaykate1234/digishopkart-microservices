import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { SAMPLE_ACCOUNTS } from '../../core/access';
import { errorMessage } from '../../core/api-error';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login-page.html',
})
export class LoginPage {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly accounts = SAMPLE_ACCOUNTS;
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly form = new FormGroup({
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  protected useAccount(email: string): void {
    this.form.controls.email.setValue(email);
    this.error.set(null);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    const value = this.form.getRawValue();
    this.auth.login(value.email, value.password).subscribe({
      next: () => {
        this.saving.set(false);
        void this.router.navigateByUrl('/');
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.error.set(errorMessage(error, 'Sign in failed.'));
      },
    });
  }
}
