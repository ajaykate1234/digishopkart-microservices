package com.digishopkart.product.api;

import java.math.BigDecimal;
import java.util.List;

public final class CatalogDtos {
  private CatalogDtos() {
  }

  public record ImageResponse(
      Integer id,
      Integer productId,
      String fileName,
      String contentType,
      boolean primaryImage,
      int sortOrder
  ) {
  }

  public record StoredImage(byte[] bytes, String contentType, String fileName) {
  }

  public record FacetCount(String name, long count) {
  }

  public record CatalogFacets(
      List<FacetCount> categories,
      List<FacetCount> brands,
      BigDecimal minPrice,
      BigDecimal maxPrice
  ) {
  }
}
