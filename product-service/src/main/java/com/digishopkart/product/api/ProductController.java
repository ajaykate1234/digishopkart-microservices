package com.digishopkart.product.api;

import com.digishopkart.product.api.ProductDtos.ProductRequest;
import com.digishopkart.product.api.ProductDtos.ProductResponse;
import com.digishopkart.product.api.ProductDtos.VariantRequest;
import com.digishopkart.product.api.ProductDtos.VariantResponse;
import com.digishopkart.product.service.ProductService;
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
public class ProductController {

  private final ProductService products;

  public ProductController(ProductService products) {
    this.products = products;
  }

  @GetMapping("/api/v1/health")
  public Map<String, String> health() {
    return Map.of("status", "UP", "application", "product-service");
  }

  @GetMapping("/api/v1/products")
  public List<ProductResponse> list() {
    return products.list();
  }

  @GetMapping("/api/v1/products/{productId}")
  public ProductResponse get(@PathVariable Integer productId) {
    return products.get(productId);
  }

  @PostMapping("/api/v1/products")
  public ProductResponse create(@Valid @RequestBody ProductRequest request) {
    return products.create(request);
  }

  @PutMapping("/api/v1/products/{productId}")
  public ProductResponse update(@PathVariable Integer productId, @Valid @RequestBody ProductRequest request) {
    return products.update(productId, request);
  }

  @GetMapping("/api/v1/variant/{productId}")
  public List<VariantResponse> variants(@PathVariable Integer productId) {
    return products.variantsFor(productId);
  }

  @PostMapping("/api/v1/variant/{productId}")
  public VariantResponse addVariant(@PathVariable Integer productId, @Valid @RequestBody VariantRequest request) {
    return products.addVariant(productId, request);
  }

  @PutMapping("/api/v1/variant/item/{variantId}")
  public VariantResponse updateVariant(@PathVariable Integer variantId, @Valid @RequestBody VariantRequest request) {
    return products.updateVariant(variantId, request);
  }
}
