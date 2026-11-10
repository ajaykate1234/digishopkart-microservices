package com.digishopkart.product.api;

import com.digishopkart.product.api.CatalogDtos.ImageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

public final class ProductDtos {
  private ProductDtos() {
  }

  public record VariantRequest(@NotBlank String name, @NotBlank String value, String status) {
  }

  public record VariantResponse(Integer id, Integer productId, String name, String value, String status) {
  }

  public record ProductRequest(
      @NotBlank String productName,
      @NotBlank String brand,
      @NotNull @PositiveOrZero BigDecimal productPrice,
      @PositiveOrZero BigDecimal mrp,
      String highlight,
      String productStatus,
      @NotBlank String productCategory,
      @Valid List<VariantRequest> variants
  ) {
  }

  public record ProductResponse(
      Integer id,
      String productName,
      String brand,
      BigDecimal productPrice,
      BigDecimal mrp,
      String highlight,
      String productStatus,
      String productCategory,
      List<VariantResponse> variants,
      List<ImageResponse> images
  ) {
  }
}
