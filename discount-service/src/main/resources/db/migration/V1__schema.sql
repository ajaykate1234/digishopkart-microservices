CREATE TABLE discounts (
  id INT NOT NULL AUTO_INCREMENT,
  coupon_name VARCHAR(80) NOT NULL,
  discount_type VARCHAR(16) NOT NULL,
  coupon_value DECIMAL(12, 2) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_discounts_coupon (coupon_name)
) ENGINE=InnoDB;
