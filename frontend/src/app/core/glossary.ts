export interface GlossaryEntry {
  term: string;
  group: 'Services' | 'Security' | 'Data' | 'Angular';
  plain: string;
  where: string;
}

export const GLOSSARY: GlossaryEntry[] = [
  {
    term: 'Microservice',
    group: 'Services',
    plain: 'One small application that owns one business area and its own database. DigiShopKart runs identity, customer, product, discount, and order as separate processes.',
    where: 'identity-service, customer-service, product-service, discount-service, order-service',
  },
  {
    term: 'API gateway',
    group: 'Services',
    plain: 'The only door the browser uses. It listens on port 8088 and forwards /api/v1 paths to the service that owns them. It does not have a database.',
    where: 'gateway',
  },
  {
    term: 'Feign client',
    group: 'Services',
    plain: 'A Java interface that looks like a controller and becomes an HTTP call. Order service uses Feign to ask customer, product, and discount whether an id exists before it saves an order. Identity uses Feign to create the shopper profile during register.',
    where: 'order-service ShopClients, identity-service CustomerClient',
  },
  {
    term: 'Database per service',
    group: 'Data',
    plain: 'Each service has its own MySQL database. An order stores customer_id and product_id. It does not use a foreign key into another database.',
    where: 'digishopkart_identity, digishopkart_customer, digishopkart_product, digishopkart_discount, digishopkart_order',
  },
  {
    term: 'DTO',
    group: 'Services',
    plain: 'The JSON shape sent across HTTP. A Feign client decodes the other service’s DTO. The order row keeps a copy of the product name and price so a later catalog edit does not rewrite history.',
    where: 'OrderItemResponse',
  },
  {
    term: 'JWT',
    group: 'Security',
    plain: 'A signed token issued by identity. The gateway forwards the Authorization header. Each service checks the same signature. The role inside the token is what the other services trust until the token expires.',
    where: 'common JwtService',
  },
  {
    term: 'Flyway',
    group: 'Data',
    plain: 'Versioned SQL that creates tables when a service starts. Each service has its own db/migration folder and its own schema history.',
    where: '*/src/main/resources/db/migration',
  },
  {
    term: 'Transaction',
    group: 'Data',
    plain: 'Writes inside one service succeed or fail together. A Feign call is a separate process, so it is not part of that database transaction. Register saves the user, calls customer, and rolls the user back if customer rejects the profile.',
    where: 'AuthService.register',
  },
  {
    term: 'Signal',
    group: 'Angular',
    plain: 'A value the screen reads. When the HTTP call finishes, the page sets the signal and Angular redraws the list.',
    where: 'product-page.ts',
  },
  {
    term: 'Interceptor',
    group: 'Angular',
    plain: 'Code that runs on every HTTP call from the UI. This one attaches the JWT before the request leaves the browser.',
    where: 'auth.interceptor.ts',
  },
];
