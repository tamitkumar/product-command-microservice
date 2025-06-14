package com.tech.brain.repository;

import com.tech.brain.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommandRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByProductCode(String productCode);
}
