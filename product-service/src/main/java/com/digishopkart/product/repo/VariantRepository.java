package com.digishopkart.product.repo;

import com.digishopkart.product.domain.VariantEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantRepository extends JpaRepository<VariantEntity, Integer> {
  List<VariantEntity> findByProductId(Integer productId);
}
