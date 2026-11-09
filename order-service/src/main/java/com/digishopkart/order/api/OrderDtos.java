package com.digishopkart.order.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public final class OrderDtos {
  private OrderDtos() {
  }

  public record PlaceOrderRequest(
      @NotNull Integer customerId,
      @NotEmpty List<Integer> productIds,
      Integer discountId
  ) {
  }

  public record StatusRequest(@NotNull String orderStatus) {
  }

  public record OrderItemResponse(Integer id, Integer productId, String productName, BigDecimal unitPrice) {
  }

  public record OrderResponse(
      Integer orderId,
      Integer customerId,
      String customerEmail,
      Integer discountId,
      String orderStatus,
      BigDecimal totalPrice,
      String activatedAt,
      String updatedAt,
      List<OrderItemResponse> items
  ) {
  }
}
