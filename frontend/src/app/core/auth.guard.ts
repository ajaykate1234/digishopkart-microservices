import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Role } from './models';
import { SessionStore } from './session.store';

export const authGuard: CanActivateFn = () => {
  const session = inject(SessionStore);
  const router = inject(Router);
  return session.signedIn() ? true : router.createUrlTree(['/login']);
};

export function roleGuard(roles: Role[]): CanActivateFn {
  return () => {
    const session = inject(SessionStore);
    const router = inject(Router);
    const role = session.user()?.role;
    if (role && roles.includes(role)) {
      return true;
    }
    return router.createUrlTree(['/']);
  };
}
