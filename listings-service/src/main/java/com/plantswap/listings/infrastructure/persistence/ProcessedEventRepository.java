package com.plantswap.listings.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEventJpaEntity, UUID> {

    default void markProcessed(UUID eventId) {
        save(new ProcessedEventJpaEntity(eventId));
    }
}
