-- Run once, as a MySQL administrator, before the services start.
--   mysql -u root -p < scripts/create-database.sql
-- JDBC on Windows often connects as 127.0.0.1, so both hosts are created.
CREATE DATABASE IF NOT EXISTS digishopkart_identity CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS digishopkart_customer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS digishopkart_product CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS digishopkart_discount CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS digishopkart_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'digishopkart'@'localhost' IDENTIFIED BY 'digishopkart';
CREATE USER IF NOT EXISTS 'digishopkart'@'127.0.0.1' IDENTIFIED BY 'digishopkart';

GRANT ALL PRIVILEGES ON digishopkart_identity.* TO 'digishopkart'@'localhost';
GRANT ALL PRIVILEGES ON digishopkart_customer.* TO 'digishopkart'@'localhost';
GRANT ALL PRIVILEGES ON digishopkart_product.* TO 'digishopkart'@'localhost';
GRANT ALL PRIVILEGES ON digishopkart_discount.* TO 'digishopkart'@'localhost';
GRANT ALL PRIVILEGES ON digishopkart_order.* TO 'digishopkart'@'localhost';

GRANT ALL PRIVILEGES ON digishopkart_identity.* TO 'digishopkart'@'127.0.0.1';
GRANT ALL PRIVILEGES ON digishopkart_customer.* TO 'digishopkart'@'127.0.0.1';
GRANT ALL PRIVILEGES ON digishopkart_product.* TO 'digishopkart'@'127.0.0.1';
GRANT ALL PRIVILEGES ON digishopkart_discount.* TO 'digishopkart'@'127.0.0.1';
GRANT ALL PRIVILEGES ON digishopkart_order.* TO 'digishopkart'@'127.0.0.1';
FLUSH PRIVILEGES;
