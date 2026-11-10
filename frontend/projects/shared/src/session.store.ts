import { computed, Injectable, signal } from '@angular/core';
import { AuthResponse } from './models';

const STORAGE_KEY = 'digishopkart.session';
const EVENT = 'digishopkart-session';

@Injectable({ providedIn: 'root' })
export class SessionStore {
  private readonly version = signal(0);

  readonly user = computed(() => {
    this.version();
    return readSession()?.user ?? null;
  });
  readonly signedIn = computed(() => this.user() !== null);

  constructor() {
    window.addEventListener(EVENT, () => this.version.update((value) => value + 1));
  }

  token(): string | null {
    return readSession()?.accessToken ?? null;
  }

  save(response: AuthResponse): void {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(response));
    window.dispatchEvent(new Event(EVENT));
  }

  clear(): void {
    sessionStorage.removeItem(STORAGE_KEY);
    window.dispatchEvent(new Event(EVENT));
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
