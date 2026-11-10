import { Routes } from '@angular/router';
import { authGuard } from '@digi/shared';
import { CheckoutPage } from './checkout-page';

export const checkoutRoutes: Routes = [
  { path: '', component: CheckoutPage, canActivate: [authGuard] },
];
