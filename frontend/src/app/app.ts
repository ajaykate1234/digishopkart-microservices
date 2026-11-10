import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CartStore, SessionStore } from '@digi/shared';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
})
export class App {
  private readonly session = inject(SessionStore);
  private readonly cart = inject(CartStore);
  private readonly router = inject(Router);

  protected readonly query = signal('');
  protected readonly user = this.session.user;
  protected readonly signedIn = this.session.signedIn;
  protected readonly isAdmin = computed(() => this.user()?.role === 'ADMIN');
  protected readonly cartCount = this.cart.count;

  protected search(): void {
    void this.router.navigate(['/catalog'], { queryParams: { q: this.query() || null } });
  }

  protected signOut(): void {
    this.session.clear();
    void this.router.navigateByUrl('/');
  }
}
