SET @mrp_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'products' AND COLUMN_NAME = 'mrp'
);
SET @sql := IF(
  @mrp_exists = 0,
  'ALTER TABLE products ADD COLUMN mrp DECIMAL(12, 2) NULL AFTER product_price',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @highlight_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'products' AND COLUMN_NAME = 'highlight'
);
SET @sql := IF(
  @highlight_exists = 0,
  'ALTER TABLE products ADD COLUMN highlight VARCHAR(180) NULL AFTER product_category',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE products
SET mrp = CASE product_name
      WHEN 'Studio Headphones' THEN 3999.00
      WHEN 'Everyday Cotton Shirt' THEN 1499.00
      ELSE product_price
    END,
    highlight = CASE product_name
      WHEN 'Studio Headphones' THEN 'Over-ear sound, 30 hour battery, foldable'
      WHEN 'Everyday Cotton Shirt' THEN 'Cotton regular fit, machine wash'
      ELSE highlight
    END
WHERE mrp IS NULL OR product_name IN ('Studio Headphones', 'Everyday Cotton Shirt');

UPDATE products SET mrp = product_price WHERE mrp IS NULL;

ALTER TABLE products MODIFY mrp DECIMAL(12, 2) NOT NULL;

CREATE TABLE IF NOT EXISTS product_images (
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

INSERT INTO products (product_name, brand, product_price, mrp, status, product_category, highlight)
SELECT 'Swift Runners', 'Northwind', 3299.00, 4999.00, 'active', 'footwares', 'Lightweight knit, cushioned sole'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Swift Runners');

INSERT INTO products (product_name, brand, product_price, mrp, status, product_category, highlight)
SELECT 'Pulse Watch', 'Harbor', 5499.00, 7999.00, 'active', 'gadgets', 'Heart rate, 5 day battery, water resistant'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Pulse Watch');

INSERT INTO products (product_name, brand, product_price, mrp, status, product_category, highlight)
SELECT 'Pocket Buds', 'Northwind', 1999.00, 2999.00, 'active', 'electronics', 'Charging case, touch controls'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Pocket Buds');

INSERT INTO products (product_name, brand, product_price, mrp, status, product_category, highlight)
SELECT 'Daily Chain', 'Lumen', 1599.00, 2499.00, 'active', 'jwelleries', 'Gold-tone chain, 45 cm'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Daily Chain');

INSERT INTO variants (product_id, name, value, status)
SELECT p.id, 'Size', '8', 'active' FROM products p
WHERE p.product_name = 'Swift Runners'
  AND NOT EXISTS (SELECT 1 FROM variants v WHERE v.product_id = p.id AND v.name = 'Size' AND v.value = '8');

INSERT INTO variants (product_id, name, value, status)
SELECT p.id, 'Colour', 'Silver', 'active' FROM products p
WHERE p.product_name = 'Pulse Watch'
  AND NOT EXISTS (SELECT 1 FROM variants v WHERE v.product_id = p.id AND v.name = 'Colour' AND v.value = 'Silver');

INSERT INTO variants (product_id, name, value, status)
SELECT p.id, 'Colour', 'White', 'active' FROM products p
WHERE p.product_name = 'Pocket Buds'
  AND NOT EXISTS (SELECT 1 FROM variants v WHERE v.product_id = p.id AND v.name = 'Colour' AND v.value = 'White');

INSERT INTO variants (product_id, name, value, status)
SELECT p.id, 'Length', '45 cm', 'active' FROM products p
WHERE p.product_name = 'Daily Chain'
  AND NOT EXISTS (SELECT 1 FROM variants v WHERE v.product_id = p.id AND v.name = 'Length' AND v.value = '45 cm');
