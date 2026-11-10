import { Routes } from '@angular/router';
import { authGuard, roleGuard } from '@digi/shared';
import { CustomersPage } from './customers-page';
import { ProfilePage } from './profile-page';

export const accountRoutes: Routes = [
  { path: '', component: ProfilePage, canActivate: [authGuard] },
  { path: 'customers', component: CustomersPage, canActivate: [roleGuard(['ADMIN'])] },
];
