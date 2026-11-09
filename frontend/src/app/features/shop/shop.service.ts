import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Customer, Discount, Product, ShopOrder } from '../../core/models';

@Injectable({ providedIn: 'root' })
export class ShopService {
  private readonly http = inject(HttpClient);

  products() {
    return this.http.get<Product[]>('/api/v1/products');
  }

  createProduct(body: unknown) {
    return this.http.post<Product>('/api/v1/products', body);
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
