INSERT INTO products (id, product_name, brand, product_price, status, product_category) VALUES
  (1, 'Studio Headphones', 'Northwind', 2499.00, 'active', 'electronics'),
  (2, 'Everyday Cotton Shirt', 'Riverloom', 899.00, 'active', 'cloths');

INSERT INTO variants (id, product_id, name, value, status) VALUES
  (1, 1, 'Colour', 'Black', 'active'),
  (2, 2, 'Size', 'M', 'active');
