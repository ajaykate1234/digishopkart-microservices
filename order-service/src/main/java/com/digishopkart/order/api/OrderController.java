package com.digishopkart.order.api;

import com.digishopkart.order.api.OrderDtos.OrderResponse;
import com.digishopkart.order.api.OrderDtos.PlaceOrderRequest;
import com.digishopkart.order.api.OrderDtos.StatusRequest;
import com.digishopkart.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

  private final OrderService orders;

  public OrderController(OrderService orders) {
    this.orders = orders;
  }

  @GetMapping("/api/v1/health")
  public Map<String, String> health() {
    return Map.of("status", "UP", "application", "order-service");
  }

  @GetMapping("/api/v1/orders")
  public List<OrderResponse> list() {
    return orders.list();
  }

  @GetMapping("/api/v1/orders/{orderId}")
  public OrderResponse get(@PathVariable Integer orderId) {
    return orders.get(orderId);
  }

  @PostMapping("/api/v1/orders")
  public OrderResponse place(@Valid @RequestBody PlaceOrderRequest request) {
    return orders.place(request);
  }

  @PutMapping("/api/v1/orders/{orderId}/status")
  public OrderResponse status(@PathVariable Integer orderId, @Valid @RequestBody StatusRequest request) {
    return orders.updateStatus(orderId, request.orderStatus());
  }
}
