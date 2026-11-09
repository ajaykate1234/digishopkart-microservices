CREATE TABLE products (
  id INT NOT NULL AUTO_INCREMENT,
  product_name VARCHAR(120) NOT NULL,
  brand VARCHAR(80) NOT NULL,
  product_price DECIMAL(12, 2) NOT NULL,
  status VARCHAR(16) NOT NULL,
  product_category VARCHAR(32) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE variants (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  name VARCHAR(80) NOT NULL,
  value VARCHAR(80) NOT NULL,
  status VARCHAR(16) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_variant_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;
