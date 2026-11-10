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

export interface ProductImage {
  id: number;
  productId: number;
  fileName: string;
  contentType: string;
  primaryImage: boolean;
  sortOrder: number;
}

export interface Product {
  id: number;
  productName: string;
  brand: string;
  productPrice: number;
  mrp: number;
  highlight: string | null;
  productStatus: string;
  productCategory: string;
  variants: Variant[];
  images: ProductImage[];
}

export interface FacetCount {
  name: string;
  count: number;
}

export interface CatalogFacets {
  categories: FacetCount[];
  brands: FacetCount[];
  minPrice: number | null;
  maxPrice: number | null;
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

export interface CartLine {
  productId: number;
  productName: string;
  brand: string;
  price: number;
  imageId: number | null;
  quantity: number;
}

export const CATEGORIES = ['electronics', 'cloths', 'footwares', 'jwelleries', 'gadgets'] as const;
export const ORDER_STATUSES = ['confirmed', 'placed', 'dispatched', 'outfordelivery', 'delivered', 'cancelled'] as const;

export function imageUrl(productId: number, imageId: number): string {
  return `/api/v1/products/${productId}/images/${imageId}`;
}

export function primaryImage(product: Product): ProductImage | undefined {
  return product.images.find((image) => image.primaryImage) ?? product.images[0];
}

export function discountPercent(price: number, mrp: number): number {
  if (!mrp || mrp <= price) {
    return 0;
  }
  return Math.round((1 - price / mrp) * 100);
}
