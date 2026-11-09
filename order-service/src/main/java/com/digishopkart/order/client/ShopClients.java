package com.digishopkart.order.client;

import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public final class ShopClients {
  private ShopClients() {
  }

  public record CustomerView(Integer id, String email, String firstName, String lastName, String customerStatus) {
  }

  public record ProductView(Integer id, String productName, BigDecimal productPrice, String productStatus) {
  }

  public record DiscountView(Integer id, String couponName, String discountType, BigDecimal couponValue) {
  }

  @FeignClient(name = "customer-service", url = "${digishopkart.customer-url}")
  public interface CustomerClient {
    @GetMapping("/api/v1/customers/{customerId}")
    CustomerView get(@PathVariable("customerId") Integer customerId);
  }

  @FeignClient(name = "product-service", url = "${digishopkart.product-url}")
  public interface ProductClient {
    @GetMapping("/api/v1/products/{productId}")
    ProductView get(@PathVariable("productId") Integer productId);
  }

  @FeignClient(name = "discount-service", url = "${digishopkart.discount-url}")
  public interface DiscountClient {
    @GetMapping("/api/v1/discounts/{discountId}")
    DiscountView get(@PathVariable("discountId") Integer discountId);
  }
}
