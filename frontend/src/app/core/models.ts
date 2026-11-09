export type Role = 'ADMIN' | 'STAFF' | 'CUSTOMER';

export interface Me {
  id: number;
  email: string;
  fullName: string;
  role: Role;
}

export interface AuthResponse {
  tokenType: string;
  accessToken: string;
  expiresInMinutes: number;
  user: Me;
}

export interface RegisterInput {
  firstName: string;
  lastName: string;
  email: string;
  mobile: string;
  password: string;
}

export interface Address {
  id: number;
  customerId: number;
  addressType: string;
  customerFullName: string;
  country: string;
  state: string;
  town: string;
  area: string;
  houseOrBuilding: string;
  landmark: string | null;
  pinCode: number;
  mobile: string;
}

export interface Customer {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  mobile: string;
  customerStatus: string;
  customerAddress: Address[];
}

export interface Variant {
  id: number;
  productId: number;
  name: string;
  value: string;
  status: string;
}

export interface Product {
  id: number;
  productName: string;
  brand: string;
  productPrice: number;
  productStatus: string;
  productCategory: string;
  variants: Variant[];
}

export interface Discount {
  id: number;
  couponName: string;
  discountType: string;
  couponValue: number;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  unitPrice: number;
}

export interface ShopOrder {
  orderId: number;
  customerId: number;
  customerEmail: string;
  discountId: number | null;
  orderStatus: string;
  totalPrice: number;
  activatedAt: string;
  updatedAt: string;
  items: OrderItem[];
}

export const CATEGORIES = ['electronics', 'cloths', 'footwares', 'jwelleries', 'gadgets'] as const;
export const ORDER_STATUSES = ['confirmed', 'placed', 'dispatched', 'outfordelivery', 'delivered', 'cancelled'] as const;
