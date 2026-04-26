package com.plantswap.listings.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/** Spring Data JPA интерфейс для избранного. */
interface SpringDataFavoriteRepository extends JpaRepository<FavoriteJpaEntity, FavoriteJpaId> {

    boolean existsByUserIdAndListingId(UUID userId, UUID listingId);

    void deleteByUserIdAndListingId(UUID userId, UUID listingId);

    @Query("SELECT f.listingId FROM FavoriteJpaEntity f WHERE f.userId = :userId ORDER BY f.createdAt DESC")
    List<UUID> findListingIdsByUserId(UUID userId,
            org.springframework.data.domain.Pageable pageable);
}
