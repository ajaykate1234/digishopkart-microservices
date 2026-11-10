package com.digishopkart.product.repo;

import com.digishopkart.product.domain.ProductImageEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Integer> {

  @Query("""
      select i.id, i.product.id, i.fileName, i.contentType, i.primaryImage, i.sortOrder
      from ProductImageEntity i
      where i.product.id in :productIds
      order by i.primaryImage desc, i.sortOrder asc, i.id asc
      """)
  List<Object[]> findMeta(List<Integer> productIds);

  Optional<ProductImageEntity> findByIdAndProductId(Integer id, Integer productId);

  @Query("select i.imageData, i.contentType, i.fileName from ProductImageEntity i where i.id = :id and i.product.id = :productId")
  List<Object[]> findBytes(Integer id, Integer productId);

  @Modifying
  @Query("update ProductImageEntity i set i.primaryImage = false where i.product.id = :productId")
  void clearPrimary(Integer productId);

  int countByProductId(Integer productId);
}
