CREATE TABLE customers (
  id INT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(60) NOT NULL,
  last_name VARCHAR(60) NOT NULL,
  email VARCHAR(255) NOT NULL,
  mobile VARCHAR(15) NOT NULL,
  status VARCHAR(16) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_customers_email (email)
) ENGINE=InnoDB;

CREATE TABLE customer_addresses (
  id INT NOT NULL AUTO_INCREMENT,
  customer_id INT NOT NULL,
  address_type VARCHAR(16) NOT NULL,
  customer_full_name VARCHAR(120) NOT NULL,
  country VARCHAR(80) NOT NULL,
  state VARCHAR(80) NOT NULL,
  town VARCHAR(80) NOT NULL,
  area VARCHAR(80) NOT NULL,
  house_or_building VARCHAR(120) NOT NULL,
  landmark VARCHAR(120) NULL,
  pin_code INT NOT NULL,
  mobile VARCHAR(15) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_address_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
) ENGINE=InnoDB;
