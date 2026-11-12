package com.digishopkart.product.seed;

import com.digishopkart.product.domain.ProductEntity;
import com.digishopkart.product.domain.ProductImageEntity;
import com.digishopkart.product.repo.ProductImageRepository;
import com.digishopkart.product.repo.ProductRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductImageSeeder implements ApplicationRunner {

  private static final Map<String, String> FILES = Map.of(
      "Studio Headphones", "headphones.jpg",
      "Everyday Cotton Shirt", "shirt.jpg",
      "Swift Runners", "shoes.jpg",
      "Pulse Watch", "watch.jpg",
      "Pocket Buds", "earbuds.jpg",
      "Daily Chain", "necklace.jpg");

  private final ProductRepository products;
  private final ProductImageRepository images;

  public ProductImageSeeder(ProductRepository products, ProductImageRepository images) {
    this.products = products;
    this.images = images;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws IOException {
    if (images.count() > 0) {
      return;
    }
    for (Map.Entry<String, String> entry : FILES.entrySet()) {
      ProductEntity product = products.findFirstByProductName(entry.getKey()).orElse(null);
      ClassPathResource resource = new ClassPathResource("seed-images/" + entry.getValue());
      if (product == null || !resource.exists()) {
        continue;
      }
      ProductImageEntity image = new ProductImageEntity();
      image.setProduct(product);
      image.setFileName(entry.getValue());
      image.setContentType("image/jpeg");
      try (InputStream input = resource.getInputStream()) {
        image.setImageData(input.readAllBytes());
      }
      image.setPrimaryImage(true);
      image.setSortOrder(0);
      images.save(image);
    }
  }
}
