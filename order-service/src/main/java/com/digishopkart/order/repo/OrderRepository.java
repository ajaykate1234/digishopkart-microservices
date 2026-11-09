package com.digishopkart.order.repo;

import com.digishopkart.order.domain.OrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
  List<OrderEntity> findByCustomerEmailIgnoreCaseOrderByOrderIdDesc(String customerEmail);
}
