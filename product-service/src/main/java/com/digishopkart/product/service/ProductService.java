package com.digishopkart.product.service;

import com.digishopkart.common.exception.ConflictException;
import com.digishopkart.common.exception.NotFoundException;
import com.digishopkart.product.api.ProductDtos.ProductRequest;
import com.digishopkart.product.api.ProductDtos.ProductResponse;
import com.digishopkart.product.api.ProductDtos.VariantRequest;
import com.digishopkart.product.api.ProductDtos.VariantResponse;
import com.digishopkart.product.domain.ProductEntity;
import com.digishopkart.product.domain.VariantEntity;
import com.digishopkart.product.repo.ProductRepository;
import com.digishopkart.product.repo.VariantRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

  static final Set<String> CATEGORIES = Set.of("electronics", "cloths", "footwares", "jwelleries", "gadgets");

  private final ProductRepository products;
  private final VariantRepository variants;

  public ProductService(ProductRepository products, VariantRepository variants) {
    this.products = products;
    this.variants = variants;
  }

  @Transactional(readOnly = true)
  public List<ProductResponse> list() {
    return products.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public ProductResponse get(Integer id) {
    return toResponse(require(id));
  }

  @Transactional
  public ProductResponse create(ProductRequest request) {
    ProductEntity product = new ProductEntity();
    apply(product, request);
    products.save(product);
    if (request.variants() != null) {
      for (VariantRequest variant : request.variants()) {
        product.getVariants().add(newVariant(product, variant));
      }
    }
    return toResponse(product);
  }

  @Transactional
  public ProductResponse update(Integer id, ProductRequest request) {
    ProductEntity product = require(id);
    apply(product, request);
    return toResponse(product);
  }

  @Transactional(readOnly = true)
  public List<VariantResponse> variantsFor(Integer productId) {
    require(productId);
    return variants.findByProductId(productId).stream().map(this::toVariant).toList();
  }

  @Transactional
  public VariantResponse addVariant(Integer productId, VariantRequest request) {
    ProductEntity product = require(productId);
    return toVariant(variants.save(newVariant(product, request)));
  }

  @Transactional
  public VariantResponse updateVariant(Integer variantId, VariantRequest request) {
    VariantEntity variant = variants.findById(variantId)
        .orElseThrow(() -> new NotFoundException("Variant " + variantId + " was not found."));
    variant.setName(request.name().trim());
    variant.setValue(request.value().trim());
    variant.setStatus(status(request.status()));
    return toVariant(variant);
  }

  private void apply(ProductEntity product, ProductRequest request) {
    String category = request.productCategory().trim().toLowerCase();
    if (!CATEGORIES.contains(category)) {
      throw new ConflictException("productCategory must be one of " + CATEGORIES + ".");
    }
    product.setProductName(request.productName().trim());
    product.setBrand(request.brand().trim());
    product.setProductPrice(request.productPrice());
    product.setProductStatus(status(request.productStatus()));
    product.setProductCategory(category);
  }

  private VariantEntity newVariant(ProductEntity product, VariantRequest request) {
    VariantEntity variant = new VariantEntity();
    variant.setProduct(product);
    variant.setName(request.name().trim());
    variant.setValue(request.value().trim());
    variant.setStatus(status(request.status()));
    return variant;
  }

  private String status(String value) {
    String status = value == null || value.isBlank() ? "active" : value.trim().toLowerCase();
    if (!status.equals("active") && !status.equals("inactive")) {
      throw new ConflictException("status must be active or inactive.");
    }
    return status;
  }

  private ProductEntity require(Integer id) {
    return products.findById(id).orElseThrow(() -> new NotFoundException("Product " + id + " was not found."));
  }

  private ProductResponse toResponse(ProductEntity product) {
    return new ProductResponse(
        product.getId(),
        product.getProductName(),
        product.getBrand(),
        product.getProductPrice(),
        product.getProductStatus(),
        product.getProductCategory(),
        product.getVariants().stream().map(this::toVariant).toList());
  }

  private VariantResponse toVariant(VariantEntity variant) {
    return new VariantResponse(variant.getId(), variant.getProduct().getId(), variant.getName(), variant.getValue(), variant.getStatus());
  }
}
