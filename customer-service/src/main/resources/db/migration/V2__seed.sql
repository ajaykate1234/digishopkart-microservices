INSERT INTO customers (id, first_name, last_name, email, mobile, status) VALUES
  (1, 'Meera', 'Customer', 'customer@digishopkart.local', '9876543210', 'active');

INSERT INTO customer_addresses (
  id, customer_id, address_type, customer_full_name, country, state, town, area,
  house_or_building, landmark, pin_code, mobile
) VALUES (
  1, 1, 'shipping', 'Meera Customer', 'India', 'Maharashtra', 'Pune', 'Kothrud',
  '12 Green Park', 'Near the library', 411038, '9876543210'
);
