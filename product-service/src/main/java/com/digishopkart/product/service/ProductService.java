package com.digishopkart.product.service;

import com.digishopkart.common.exception.ConflictException;
import com.digishopkart.common.exception.NotFoundException;
import com.digishopkart.product.api.CatalogDtos.CatalogFacets;
import com.digishopkart.product.api.CatalogDtos.FacetCount;
import com.digishopkart.product.api.CatalogDtos.ImageResponse;
import com.digishopkart.product.api.CatalogDtos.StoredImage;
import com.digishopkart.product.api.ProductDtos.ProductRequest;
import com.digishopkart.product.api.ProductDtos.ProductResponse;
import com.digishopkart.product.api.ProductDtos.VariantRequest;
import com.digishopkart.product.api.ProductDtos.VariantResponse;
import com.digishopkart.product.domain.ProductEntity;
import com.digishopkart.product.domain.ProductImageEntity;
import com.digishopkart.product.domain.VariantEntity;
import com.digishopkart.product.repo.ProductImageRepository;
import com.digishopkart.product.repo.ProductRepository;
import com.digishopkart.product.repo.VariantRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

  static final Set<String> CATEGORIES = Set.of("electronics", "cloths", "footwares", "jwelleries", "gadgets");

  private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

  private final ProductRepository products;
  private final VariantRepository variants;
  private final ProductImageRepository images;

  public ProductService(ProductRepository products, VariantRepository variants, ProductImageRepository images) {
    this.products = products;
    this.variants = variants;
    this.images = images;
  }

  @Transactional(readOnly = true)
  public List<ProductResponse> search(String q, List<String> categories, List<String> brands, BigDecimal minPrice, BigDecimal maxPrice) {
    String query = q == null || q.isBlank() ? null : q.trim();
    List<String> categoryFilter = clean(categories);
    List<String> brandFilter = clean(brands);
    List<ProductEntity> found = products.search(
        query,
        categoryFilter.isEmpty(),
        categoryFilter.isEmpty() ? List.of("-") : categoryFilter,
        brandFilter.isEmpty(),
        brandFilter.isEmpty() ? List.of("-") : brandFilter,
        minPrice,
        maxPrice,
        "active");
    return withImages(found);
  }

  @Transactional(readOnly = true)
  public CatalogFacets facets() {
    List<FacetCount> categories = products.categoryCounts().stream()
        .map(row -> new FacetCount(String.valueOf(row[0]), ((Number) row[1]).longValue()))
        .toList();
    List<FacetCount> brands = products.brandCounts().stream()
        .map(row -> new FacetCount(String.valueOf(row[0]), ((Number) row[1]).longValue()))
        .toList();
    List<Object[]> range = products.priceRange();
    BigDecimal min = null;
    BigDecimal max = null;
    if (!range.isEmpty() && range.get(0)[0] != null) {
      min = new BigDecimal(range.get(0)[0].toString());
      max = new BigDecimal(range.get(0)[1].toString());
    }
    return new CatalogFacets(categories, brands, min, max);
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
    BigDecimal mrp = request.mrp() == null ? request.productPrice() : request.mrp();
    if (mrp.compareTo(request.productPrice()) < 0) {
      throw new ConflictException("mrp cannot be lower than the selling price.");
    }
    product.setMrp(mrp);
    product.setHighlight(request.highlight() == null || request.highlight().isBlank() ? null : request.highlight().trim());
    product.setProductStatus(status(request.productStatus()));
    product.setProductCategory(category);
  }

  @Transactional
  public ImageResponse storeImage(Integer productId, MultipartFile file, boolean primary) {
    if (file == null || file.isEmpty()) {
      throw new ConflictException("Choose an image file.");
    }
    String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
    if (!IMAGE_TYPES.contains(type)) {
      throw new ConflictException("Image must be jpeg, png, or webp.");
    }
    ProductEntity product = require(productId);
    if (primary || images.countByProductId(productId) == 0) {
      images.clearPrimary(productId);
      primary = true;
    }
    ProductImageEntity image = new ProductImageEntity();
    image.setProduct(product);
    image.setFileName(file.getOriginalFilename() == null || file.getOriginalFilename().isBlank() ? "product.jpg" : file.getOriginalFilename());
    image.setContentType(type);
    try {
      image.setImageData(file.getBytes());
    } catch (java.io.IOException exception) {
      throw new ConflictException("The image could not be read.");
    }
    image.setPrimaryImage(primary);
    image.setSortOrder(images.countByProductId(productId));
    ProductImageEntity saved = images.save(image);
    return toImage(saved);
  }

  @Transactional(readOnly = true)
  public StoredImage readImage(Integer productId, Integer imageId) {
    List<Object[]> rows = images.findBytes(imageId, productId);
    if (rows.isEmpty()) {
      throw new NotFoundException("Image " + imageId + " was not found.");
    }
    Object[] row = rows.get(0);
    return new StoredImage((byte[]) row[0], String.valueOf(row[1]), String.valueOf(row[2]));
  }

  @Transactional
  public void deleteImage(Integer productId, Integer imageId) {
    ProductImageEntity image = images.findByIdAndProductId(imageId, productId)
        .orElseThrow(() -> new NotFoundException("Image " + imageId + " was not found."));
    boolean wasPrimary = image.isPrimaryImage();
    images.delete(image);
    images.flush();
    if (wasPrimary) {
      images.findMeta(List.of(productId)).stream().findFirst().ifPresent(row -> {
        Integer nextId = ((Number) row[0]).intValue();
        images.findById(nextId).ifPresent(next -> next.setPrimaryImage(true));
      });
    }
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

  private List<ProductResponse> withImages(List<ProductEntity> found) {
    Map<Integer, List<ImageResponse>> grouped = groupImages(found.stream().map(ProductEntity::getId).toList());
    return found.stream().map(product -> toResponse(product, grouped.getOrDefault(product.getId(), List.of()))).toList();
  }

  private Map<Integer, List<ImageResponse>> groupImages(List<Integer> productIds) {
    Map<Integer, List<ImageResponse>> grouped = new LinkedHashMap<>();
    if (productIds.isEmpty()) {
      return grouped;
    }
    for (Object[] row : images.findMeta(productIds)) {
      Integer productId = ((Number) row[1]).intValue();
      grouped.computeIfAbsent(productId, ignored -> new ArrayList<>()).add(new ImageResponse(
          ((Number) row[0]).intValue(),
          productId,
          String.valueOf(row[2]),
          String.valueOf(row[3]),
          Boolean.TRUE.equals(row[4]) || (row[4] instanceof Number number && number.intValue() == 1),
          ((Number) row[5]).intValue()));
    }
    return grouped;
  }

  private ProductResponse toResponse(ProductEntity product) {
    return toResponse(product, groupImages(List.of(product.getId())).getOrDefault(product.getId(), List.of()));
  }

  private ProductResponse toResponse(ProductEntity product, List<ImageResponse> pictures) {
    return new ProductResponse(
        product.getId(),
        product.getProductName(),
        product.getBrand(),
        product.getProductPrice(),
        product.getMrp(),
        product.getHighlight(),
        product.getProductStatus(),
        product.getProductCategory(),
        product.getVariants().stream().map(this::toVariant).toList(),
        pictures);
  }

  private ImageResponse toImage(ProductImageEntity image) {
    return new ImageResponse(
        image.getId(),
        image.getProduct().getId(),
        image.getFileName(),
        image.getContentType(),
        image.isPrimaryImage(),
        image.getSortOrder());
  }

  private VariantResponse toVariant(VariantEntity variant) {
    return new VariantResponse(variant.getId(), variant.getProduct().getId(), variant.getName(), variant.getValue(), variant.getStatus());
  }

  private List<String> clean(List<String> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream().filter(value -> value != null && !value.isBlank()).map(String::trim).toList();
  }
}
