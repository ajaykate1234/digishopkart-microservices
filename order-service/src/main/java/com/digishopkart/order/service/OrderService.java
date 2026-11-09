package com.digishopkart.order.service;

import com.digishopkart.common.exception.ConflictException;
import com.digishopkart.common.exception.NotFoundException;
import com.digishopkart.common.security.CurrentUser;
import com.digishopkart.order.api.OrderDtos.OrderItemResponse;
import com.digishopkart.order.api.OrderDtos.OrderResponse;
import com.digishopkart.order.api.OrderDtos.PlaceOrderRequest;
import com.digishopkart.order.client.ShopClients.CustomerClient;
import com.digishopkart.order.client.ShopClients.CustomerView;
import com.digishopkart.order.client.ShopClients.DiscountClient;
import com.digishopkart.order.client.ShopClients.DiscountView;
import com.digishopkart.order.client.ShopClients.ProductClient;
import com.digishopkart.order.client.ShopClients.ProductView;
import com.digishopkart.order.domain.OrderEntity;
import com.digishopkart.order.domain.OrderItemEntity;
import com.digishopkart.order.repo.OrderRepository;
import feign.FeignException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  private static final Set<String> STATUSES = Set.of(
      "confirmed", "placed", "dispatched", "outfordelivery", "delivered", "cancelled");

  private final OrderRepository orders;
  private final CustomerClient customers;
  private final ProductClient products;
  private final DiscountClient discounts;

  public OrderService(OrderRepository orders, CustomerClient customers, ProductClient products, DiscountClient discounts) {
    this.orders = orders;
    this.customers = customers;
    this.products = products;
    this.discounts = discounts;
  }

  @Transactional
  public OrderResponse place(PlaceOrderRequest request) {
    CustomerView customer = call(() -> customers.get(request.customerId()), "Customer", request.customerId());
    if (!"active".equalsIgnoreCase(customer.customerStatus())) {
      throw new ConflictException("Customer " + customer.id() + " is not active.");
    }
    if (!staff() && !customer.email().equalsIgnoreCase(CurrentUser.require().getUsername())) {
      throw new AccessDeniedException("order");
    }

    List<ProductView> lines = new ArrayList<>();
    BigDecimal sum = BigDecimal.ZERO;
    for (Integer productId : request.productIds()) {
      ProductView product = call(() -> products.get(productId), "Product", productId);
      if (!"active".equalsIgnoreCase(product.productStatus())) {
        throw new ConflictException("Product " + productId + " is not active.");
      }
      lines.add(product);
      sum = sum.add(product.productPrice());
    }

    Integer discountId = null;
    if (request.discountId() != null) {
      DiscountView discount = call(() -> discounts.get(request.discountId()), "Discount", request.discountId());
      discountId = discount.id();
      sum = applyDiscount(sum, discount);
    }

    String now = Instant.now().toString();
    OrderEntity order = new OrderEntity();
    order.setCustomerId(customer.id());
    order.setCustomerEmail(customer.email());
    order.setDiscountId(discountId);
    order.setOrderStatus("confirmed");
    order.setTotalPrice(sum.setScale(2, RoundingMode.HALF_UP));
    order.setActivatedAt(now);
    order.setUpdatedAt(now);
    for (ProductView product : lines) {
      OrderItemEntity item = new OrderItemEntity();
      item.setOrder(order);
      item.setProductId(product.id());
      item.setProductName(product.productName());
      item.setUnitPrice(product.productPrice());
      order.getItems().add(item);
    }
    return toResponse(orders.save(order));
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> list() {
    List<OrderEntity> rows = staff()
        ? orders.findAll()
        : orders.findByCustomerEmailIgnoreCaseOrderByOrderIdDesc(CurrentUser.require().getUsername());
    return rows.stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public OrderResponse get(Integer orderId) {
    OrderEntity order = require(orderId);
    if (!staff() && !order.getCustomerEmail().equalsIgnoreCase(CurrentUser.require().getUsername())) {
      throw new AccessDeniedException("order");
    }
    return toResponse(order);
  }

  @Transactional
  public OrderResponse updateStatus(Integer orderId, String status) {
    if (!CurrentUser.hasRole("ADMIN")) {
      throw new AccessDeniedException("status");
    }
    String next = status == null ? "" : status.trim().toLowerCase();
    if (!STATUSES.contains(next)) {
      throw new ConflictException("orderStatus must be one of " + STATUSES + ".");
    }
    OrderEntity order = require(orderId);
    order.setOrderStatus(next);
    order.setUpdatedAt(Instant.now().toString());
    return toResponse(order);
  }

  private BigDecimal applyDiscount(BigDecimal sum, DiscountView discount) {
    if ("FLAT".equalsIgnoreCase(discount.discountType())) {
      return sum.subtract(discount.couponValue()).max(BigDecimal.ZERO);
    }
    if ("PERCENTAGE".equalsIgnoreCase(discount.discountType())) {
      BigDecimal off = sum.multiply(discount.couponValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
      return sum.subtract(off).max(BigDecimal.ZERO);
    }
    throw new ConflictException("Discount " + discount.id() + " has an unknown type.");
  }

  private <T> T call(java.util.function.Supplier<T> call, String label, Integer id) {
    try {
      return call.get();
    } catch (FeignException.NotFound exception) {
      throw new NotFoundException(label + " " + id + " was not found.");
    } catch (FeignException exception) {
      throw new ConflictException(label + " service rejected the check (HTTP " + exception.status() + ").");
    }
  }

  private boolean staff() {
    return CurrentUser.hasRole("ADMIN") || CurrentUser.hasRole("STAFF");
  }

  private OrderEntity require(Integer orderId) {
    return orders.findById(orderId).orElseThrow(() -> new NotFoundException("Order " + orderId + " was not found."));
  }

  private OrderResponse toResponse(OrderEntity order) {
    return new OrderResponse(
        order.getOrderId(),
        order.getCustomerId(),
        order.getCustomerEmail(),
        order.getDiscountId(),
        order.getOrderStatus(),
        order.getTotalPrice(),
        order.getActivatedAt(),
        order.getUpdatedAt(),
        order.getItems().stream()
            .map(item -> new OrderItemResponse(item.getId(), item.getProductId(), item.getProductName(), item.getUnitPrice()))
            .toList());
  }
}
