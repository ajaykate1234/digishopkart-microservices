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

  private static final Map<Integer, String> FILES = Map.of(
      1, "headphones.jpg",
      2, "shirt.jpg",
      3, "shoes.jpg",
      4, "watch.jpg",
      5, "earbuds.jpg",
      6, "necklace.jpg");

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
    for (Map.Entry<Integer, String> entry : FILES.entrySet()) {
      ProductEntity product = products.findById(entry.getKey()).orElse(null);
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
