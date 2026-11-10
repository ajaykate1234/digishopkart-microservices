import { loadRemoteModule } from '@angular-architects/native-federation';
import { Routes } from '@angular/router';
import { LoginPage } from './features/auth/login-page';
import { RegisterPage } from './features/auth/register-page';
import { GlossaryPage } from './features/glossary/glossary-page';
import { HomePage } from './features/home/home-page';

export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'glossary', component: GlossaryPage },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },
  {
    path: 'catalog',
    loadChildren: () => loadRemoteModule('catalog', './routes').then((remote) => remote.catalogRoutes),
  },
  {
    path: 'account',
    loadChildren: () => loadRemoteModule('account', './routes').then((remote) => remote.accountRoutes),
  },
  {
    path: 'checkout',
    loadChildren: () => loadRemoteModule('checkout', './routes').then((remote) => remote.checkoutRoutes),
  },
  {
    path: 'offers',
    loadChildren: () => loadRemoteModule('offers', './routes').then((remote) => remote.offerRoutes),
  },
  { path: '**', redirectTo: '' },
];
