package com.plantswap.deals.infrastructure.persistence;

import com.plantswap.deals.domain.model.DealStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/** Spring Data JPA интерфейс для сделок. */
interface SpringDataDealRepository extends JpaRepository<DealJpaEntity, UUID> {

    @Query("SELECT d FROM DealJpaEntity d WHERE d.ownerId = :userId OR d.requesterId = :userId ORDER BY d.createdAt DESC")
    List<DealJpaEntity> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM DealJpaEntity d WHERE d.ownerId = :userId OR d.requesterId = :userId")
    long countByUserId(UUID userId);

    @Query("SELECT d FROM DealJpaEntity d WHERE d.listingId = :listingId AND d.requesterId = :requesterId AND d.status IN :activeStatuses")
    List<DealJpaEntity> findActiveByListingIdAndRequesterId(
            UUID listingId, UUID requesterId, List<String> activeStatuses);
}
