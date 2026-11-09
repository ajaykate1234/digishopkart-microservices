package com.digishopkart.identity.repo;

import com.digishopkart.identity.domain.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);
}
