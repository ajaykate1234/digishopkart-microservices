package com.digishopkart.discount.repo;

import com.digishopkart.discount.domain.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<DiscountEntity, Integer> {
}
