import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { AuthResponse, RegisterInput } from '../../core/models';
import { SessionStore } from '../../core/session.store';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly session = inject(SessionStore);

  login(email: string, password: string) {
    return this.http
      .post<AuthResponse>('/api/v1/auth/login', { email, password })
      .pipe(tap((response) => this.session.save(response)));
  }

  register(input: RegisterInput) {
    return this.http
      .post<AuthResponse>('/api/v1/auth/register', input)
      .pipe(tap((response) => this.session.save(response)));
  }
}
