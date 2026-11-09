package com.digishopkart.customer.repo;

import com.digishopkart.customer.domain.CustomerAddressEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<CustomerAddressEntity, Integer> {
  List<CustomerAddressEntity> findByCustomerId(Integer customerId);
}
