CREATE TABLE orders (
  order_id INT NOT NULL AUTO_INCREMENT,
  customer_id INT NOT NULL,
  customer_email VARCHAR(255) NOT NULL,
  discount_id INT NULL,
  status VARCHAR(32) NOT NULL,
  total_price DECIMAL(12, 2) NOT NULL,
  activated_at VARCHAR(40) NOT NULL,
  updated_at VARCHAR(40) NOT NULL,
  PRIMARY KEY (order_id)
) ENGINE=InnoDB;

CREATE TABLE order_items (
  id INT NOT NULL AUTO_INCREMENT,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  product_name VARCHAR(120) NOT NULL,
  unit_price DECIMAL(12, 2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders (order_id)
) ENGINE=InnoDB;
