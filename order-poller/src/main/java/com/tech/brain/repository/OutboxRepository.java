package com.tech.brain.repository;

import com.tech.brain.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    List<OutboxEntity> findByProcessedFalse();
}
