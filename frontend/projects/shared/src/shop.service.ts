import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CatalogFacets, Customer, Discount, Product, ShopOrder } from './models';

export interface ProductQuery {
  q?: string;
  category?: string[];
  brand?: string[];
  minPrice?: number | null;
  maxPrice?: number | null;
}

@Injectable({ providedIn: 'root' })
export class ShopService {
  private readonly http = inject(HttpClient);

  products(query: ProductQuery = {}) {
    let params = new HttpParams();
    if (query.q) {
      params = params.set('q', query.q);
    }
    for (const category of query.category ?? []) {
      params = params.append('category', category);
    }
    for (const brand of query.brand ?? []) {
      params = params.append('brand', brand);
    }
    if (query.minPrice != null) {
      params = params.set('minPrice', query.minPrice);
    }
    if (query.maxPrice != null) {
      params = params.set('maxPrice', query.maxPrice);
    }
    return this.http.get<Product[]>('/api/v1/products', { params });
  }

  product(id: number) {
    return this.http.get<Product>(`/api/v1/products/${id}`);
  }

  createProduct(body: unknown) {
    return this.http.post<Product>('/api/v1/products', body);
  }

  facets() {
    return this.http.get<CatalogFacets>('/api/v1/catalog/facets');
  }

  uploadImage(productId: number, file: File) {
    const body = new FormData();
    body.set('file', file);
    body.set('primary', 'false');
    return this.http.post(`/api/v1/products/${productId}/images`, body);
  }

  customers() {
    return this.http.get<Customer[]>('/api/v1/customers');
  }

  me() {
    return this.http.get<Customer>('/api/v1/customers/me');
  }

  updateCustomer(id: number, body: unknown) {
    return this.http.put<Customer>(`/api/v1/customers/${id}`, body);
  }

  addAddress(body: unknown) {
    return this.http.post('/api/v1/customerAddress', body);
  }

  discounts() {
    return this.http.get<Discount[]>('/api/v1/discounts');
  }

  createDiscount(body: unknown) {
    return this.http.post<Discount>('/api/v1/discounts', body);
  }

  orders() {
    return this.http.get<ShopOrder[]>('/api/v1/orders');
  }

  placeOrder(body: unknown) {
    return this.http.post<ShopOrder>('/api/v1/orders', body);
  }

  updateOrderStatus(orderId: number, orderStatus: string) {
    return this.http.put<ShopOrder>(`/api/v1/orders/${orderId}/status`, { orderStatus });
  }
}
