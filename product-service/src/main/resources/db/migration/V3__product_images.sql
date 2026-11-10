ALTER TABLE products
  ADD COLUMN mrp DECIMAL(12, 2) NULL AFTER product_price,
  ADD COLUMN highlight VARCHAR(180) NULL AFTER product_category;

UPDATE products
SET mrp = CASE id
      WHEN 1 THEN 3999.00
      WHEN 2 THEN 1499.00
      ELSE product_price
    END,
    highlight = CASE id
      WHEN 1 THEN 'Over-ear sound, 30 hour battery, foldable'
      WHEN 2 THEN 'Cotton regular fit, machine wash'
      ELSE highlight
    END;

ALTER TABLE products MODIFY mrp DECIMAL(12, 2) NOT NULL;

CREATE TABLE product_images (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  file_name VARCHAR(180) NOT NULL,
  content_type VARCHAR(80) NOT NULL,
  image_data LONGBLOB NOT NULL,
  primary_image BIT(1) NOT NULL,
  sort_order INT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_product_images_product (product_id),
  CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;

INSERT INTO products (id, product_name, brand, product_price, mrp, status, product_category, highlight) VALUES
  (3, 'Swift Runners', 'Northwind', 3299.00, 4999.00, 'active', 'footwares', 'Lightweight knit, cushioned sole'),
  (4, 'Pulse Watch', 'Harbor', 5499.00, 7999.00, 'active', 'gadgets', 'Heart rate, 5 day battery, water resistant'),
  (5, 'Pocket Buds', 'Northwind', 1999.00, 2999.00, 'active', 'electronics', 'Charging case, touch controls'),
  (6, 'Daily Chain', 'Lumen', 1599.00, 2499.00, 'active', 'jwelleries', 'Gold-tone chain, 45 cm');

INSERT INTO variants (id, product_id, name, value, status) VALUES
  (3, 3, 'Size', '8', 'active'),
  (4, 4, 'Colour', 'Silver', 'active'),
  (5, 5, 'Colour', 'White', 'active'),
  (6, 6, 'Length', '45 cm', 'active');
