import { Routes } from '@angular/router';
import { CatalogPage } from './catalog-page';
import { ProductDetailPage } from './product-detail-page';

export const catalogRoutes: Routes = [
  { path: '', component: CatalogPage },
  { path: ':productId', component: ProductDetailPage },
];
