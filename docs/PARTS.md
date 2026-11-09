# DigiShopKart parts

The running layout is the service split described in the README.

- Gateway is the only public HTTP entry.
- Identity owns login and roles.
- Customer owns shoppers and addresses.
- Product owns the catalog and variants. Brand and category are columns, not master tables.
- Discount owns coupons.
- Order owns orders and line items. It checks customer, product, and discount ids with Feign before saving.
- The Angular app calls `/api/v1` through the gateway.

Subscription from the older single project is not included.
