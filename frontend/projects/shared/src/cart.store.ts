import { computed, Injectable, signal } from '@angular/core';
import { CartLine } from './models';

const STORAGE_KEY = 'digishopkart.cart';
const EVENT = 'digishopkart-cart';

@Injectable({ providedIn: 'root' })
export class CartStore {
  private readonly version = signal(0);
  readonly lines = computed(() => {
    this.version();
    return readCart();
  });
  readonly count = computed(() => this.lines().reduce((sum, line) => sum + line.quantity, 0));

  constructor() {
    window.addEventListener(EVENT, () => this.version.update((value) => value + 1));
  }

  add(line: Omit<CartLine, 'quantity'>): void {
    const lines = readCart();
    const existing = lines.find((item) => item.productId === line.productId);
    if (existing) {
      existing.quantity += 1;
    } else {
      lines.push({ ...line, quantity: 1 });
    }
    writeCart(lines);
  }

  setQuantity(productId: number, quantity: number): void {
    const lines = readCart()
      .map((line) => (line.productId === productId ? { ...line, quantity } : line))
      .filter((line) => line.quantity > 0);
    writeCart(lines);
  }

  clear(): void {
    writeCart([]);
  }
}

function readCart(): CartLine[] {
  const raw = sessionStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return [];
  }
  try {
    return JSON.parse(raw) as CartLine[];
  } catch {
    return [];
  }
}

function writeCart(lines: CartLine[]): void {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(lines));
  window.dispatchEvent(new Event(EVENT));
}
