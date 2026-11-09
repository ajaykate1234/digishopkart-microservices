# DigiShopKart

A shop split into services so each module owns its tables. The browser talks only to the gateway. Services talk to each other with Feign.

| Process | Port | Database | Owns |
| --- | --- | --- | --- |
| gateway | 8088 | none | Routes `/api/v1/**` |
| identity-service | 8081 | digishopkart_identity | `users`, `roles`, JWT |
| customer-service | 8082 | digishopkart_customer | `customers`, `customer_addresses` |
| product-service | 8083 | digishopkart_product | `products`, `variants` |
| discount-service | 8084 | digishopkart_discount | `discounts` |
| order-service | 8085 | digishopkart_order | `orders`, `order_items` |
| Angular UI | 4217 | none | Calls the gateway through the dev proxy |

Address stays inside the customer service. Variant stays inside the product service. An order stores `customer_id`, `discount_id`, and one `order_items` row per `product_id`. There is no foreign key across databases. On place-order, the order service calls the other three with Feign and checks that those ids exist.

The old subscription API is not in this split. Login lives in identity because the shop needs roles. The old project had no login table.

## Sample accounts

Flyway inserts these into the identity database. The customer database also has a profile for the customer email.

| Role | Email | Password |
| --- | --- | --- |
| ADMIN | admin@digishopkart.local | Admin@123 |
| STAFF | staff@digishopkart.local | Staff@123 |
| CUSTOMER | customer@digishopkart.local | Customer@123 |

Registration creates a CUSTOMER login and, through Feign, a customer profile. It cannot create an admin.

## Run it

You need JDK 21, Maven, MySQL 8, and Node 22.

Create the databases once, as a MySQL administrator. On Windows, `mysql` is often not on PATH. Use the full path to `mysql.exe` from MySQL Server 26.10. The app user is `digishopkart` / `digishopkart`, not root.

```bash
mysql -u root -p < scripts/create-database.sql
```

Docker alternative:

```bash
docker compose up -d
```

Build and start the services. Start customer, product, and discount before identity and order, because register and place-order call them.

```bash
mvn -DskipTests package
java -jar customer-service/target/customer-service-0.2.0.jar
java -jar product-service/target/product-service-0.2.0.jar
java -jar discount-service/target/discount-service-0.2.0.jar
java -jar identity-service/target/identity-service-0.2.0.jar
java -jar order-service/target/order-service-0.2.0.jar
java -jar gateway/target/gateway-0.2.0.jar
```

Each command needs its own terminal. The UI:

```bash
cd frontend
npm install
npm start
```

Open `http://127.0.0.1:4217`. The dev server proxies `/api` to the gateway on port 8088.

## What the screens do

- Products and discounts can be read without signing in.
- Staff and admin can add a product with one variant.
- Admin can add a coupon and mark a customer inactive.
- A signed-in shopper can add an address and place an order. The total applies a FLAT amount or a PERCENTAGE.
- Admin can move an order through `confirmed`, `placed`, `dispatched`, `outfordelivery`, `delivered`, and `cancelled`.

## Choices kept from the draft

- One MySQL server, a separate database per service.
- The same local JWT secret on every service (`APP_JWT_SECRET` overrides it).
- The gateway does not check the token. Each service does, so a direct call to a service port is still protected.
- Feign forwards the caller's `Authorization` header.
- Category values stay `electronics`, `cloths`, `footwares`, `jwelleries`, `gadgets`. Discount types stay `FLAT` and `PERCENTAGE`.
