import { Component, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { SessionStore } from './core/session.store';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
})
export class App {
  private readonly session = inject(SessionStore);
  private readonly router = inject(Router);

  protected readonly navOpen = signal(false);
  protected readonly user = this.session.user;
  protected readonly signedIn = this.session.signedIn;
  protected readonly isAdmin = computed(() => this.user()?.role === 'ADMIN');

  constructor() {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      this.navOpen.set(false);
    });
  }

  protected signOut(): void {
    this.session.clear();
    void this.router.navigateByUrl('/');
  }
}
