# Technical terms in DigiShopKart

The Terms screen in the UI is the short list. This file is the study copy for the service split.

## Services

| Service | What it owns |
| --- | --- |
| gateway | Routes. No tables. |
| identity-service | `users` and `roles`. Issues the JWT. |
| customer-service | `customers` and `customer_addresses`. |
| product-service | `products` and `variants`. |
| discount-service | `discounts`. |
| order-service | `orders` and `order_items`. |

A request from the browser hits the gateway. The gateway forwards the path and the `Authorization` header. The service that owns the data answers.

## Feign

A Feign client is an interface. Spring turns each method into an HTTP call.

- Register: identity calls `POST /api/v1/customers` on the customer service.
- Place order: order calls customer, product, and discount, then saves ids in its own database.

The caller's JWT is copied onto the Feign request. The other service checks it. A missing id is a 404 from that service, which the order service turns into its own 404.

## Data

Each service has its own database and its own Flyway scripts. Hibernate checks the tables (`ddl-auto: validate`) and does not create them.

An order does not join to `customers` or `products`. Those tables are in other databases. The line item stores `product_id`, `product_name`, and `unit_price` so the receipt stays the same if the catalog changes later.

## Security

Authentication is `POST /api/v1/auth/login`. Identity checks the BCrypt hash and returns a JWT.

Authorization is the role inside that token: `ADMIN`, `STAFF`, or `CUSTOMER`. Each service reads the signature with the shared local secret. Changing a role in the database applies on the next login, not on a token that is already issued.

## Angular

The UI uses signals for the lists. The interceptor adds the bearer token. The dev server proxies `/api` to port 8088, so the browser never calls 8081–8085 directly.
