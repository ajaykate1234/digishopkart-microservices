import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth.guard';
import { LoginPage } from './features/auth/login-page';
import { RegisterPage } from './features/auth/register-page';
import { CustomerPage } from './features/customer/customer-page';
import { ProfilePage } from './features/customer/profile-page';
import { DiscountPage } from './features/discount/discount-page';
import { GlossaryPage } from './features/glossary/glossary-page';
import { HomePage } from './features/home/home-page';
import { OrderPage } from './features/order/order-page';
import { ProductPage } from './features/product/product-page';

export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'glossary', component: GlossaryPage },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'products', component: ProductPage },
  { path: 'discounts', component: DiscountPage },
  { path: 'orders', component: OrderPage, canActivate: [authGuard] },
  { path: 'customers', component: CustomerPage, canActivate: [roleGuard(['ADMIN'])] },
  { path: 'profile', component: ProfilePage, canActivate: [authGuard] },
  { path: '**', redirectTo: '' },
];
