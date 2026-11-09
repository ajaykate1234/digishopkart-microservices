export interface AccessRow {
  action: string;
  public: boolean;
  customer: boolean;
  staff: boolean;
  admin: boolean;
}

export const ACCESS_ROWS: AccessRow[] = [
  { action: 'Read products and discounts', public: true, customer: true, staff: true, admin: true },
  { action: 'Add or edit a product or variant', public: false, customer: false, staff: true, admin: true },
  { action: 'Add or edit a discount', public: false, customer: false, staff: false, admin: true },
  { action: 'Read every customer', public: false, customer: false, staff: false, admin: true },
  { action: 'Edit own profile and address', public: false, customer: true, staff: false, admin: true },
  { action: 'Place an order', public: false, customer: true, staff: true, admin: true },
  { action: 'Change order status', public: false, customer: false, staff: false, admin: true },
];

export const SAMPLE_ACCOUNTS = [
  { role: 'ADMIN', email: 'admin@digishopkart.local', note: 'Customers, discounts, catalog, and order status' },
  { role: 'STAFF', email: 'staff@digishopkart.local', note: 'Catalog and variants. Reads every order' },
  { role: 'CUSTOMER', email: 'customer@digishopkart.local', note: 'Own profile, own orders, public catalog' },
];
