package com.digishopkart.identity.repo;

import com.digishopkart.identity.domain.RoleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
  Optional<RoleEntity> findByCode(String code);
}
