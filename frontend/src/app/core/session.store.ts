import { computed, Injectable, signal } from '@angular/core';
import { AuthResponse } from './models';

const STORAGE_KEY = 'digishopkart.session';

@Injectable({ providedIn: 'root' })
export class SessionStore {
  private readonly session = signal<AuthResponse | null>(readSession());

  readonly user = computed(() => this.session()?.user ?? null);
  readonly signedIn = computed(() => this.user() !== null);

  token(): string | null {
    return this.session()?.accessToken ?? null;
  }

  save(response: AuthResponse): void {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(response));
    this.session.set(response);
  }

  clear(): void {
    sessionStorage.removeItem(STORAGE_KEY);
    this.session.set(null);
  }
}

function readSession(): AuthResponse | null {
  const raw = sessionStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw) as AuthResponse;
  } catch {
    sessionStorage.removeItem(STORAGE_KEY);
    return null;
  }
}
