package com.digishopkart.product.api;

import com.digishopkart.product.api.CatalogDtos.CatalogFacets;
import com.digishopkart.product.api.CatalogDtos.ImageResponse;
import com.digishopkart.product.api.CatalogDtos.StoredImage;
import com.digishopkart.product.api.ProductDtos.ProductRequest;
import com.digishopkart.product.api.ProductDtos.ProductResponse;
import com.digishopkart.product.api.ProductDtos.VariantRequest;
import com.digishopkart.product.api.ProductDtos.VariantResponse;
import com.digishopkart.product.service.ProductService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
  public List<ProductResponse> list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) List<String> category,
      @RequestParam(required = false) List<String> brand,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice
  ) {
    return products.search(q, category, brand, minPrice, maxPrice);
  }

  @GetMapping("/api/v1/catalog/facets")
  public CatalogFacets facets() {
    return products.facets();
  }

  @GetMapping("/api/v1/products/{productId}/images/{imageId}")
  public ResponseEntity<byte[]> image(@PathVariable Integer productId, @PathVariable Integer imageId) {
    StoredImage stored = products.readImage(productId, imageId);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(stored.contentType()))
        .cacheControl(CacheControl.noCache())
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + stored.fileName().replace("\"", "") + "\"")
        .body(stored.bytes());
  }

  @PostMapping(value = "/api/v1/products/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ImageResponse upload(
      @PathVariable Integer productId,
      @RequestParam("file") MultipartFile file,
      @RequestParam(defaultValue = "false") boolean primary
  ) {
    return products.storeImage(productId, file, primary);
  }

  @DeleteMapping("/api/v1/products/{productId}/images/{imageId}")
  public void deleteImage(@PathVariable Integer productId, @PathVariable Integer imageId) {
    products.deleteImage(productId, imageId);
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
