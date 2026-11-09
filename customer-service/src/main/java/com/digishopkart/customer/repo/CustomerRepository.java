package com.digishopkart.customer.repo;

import com.digishopkart.customer.domain.CustomerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
  Optional<CustomerEntity> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);
}
