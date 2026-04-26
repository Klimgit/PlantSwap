package com.plantswap.listings.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** JPA-сущность таблицы favorites. */
@Entity
@Table(name = "favorites")
@IdClass(FavoriteJpaId.class)
public class FavoriteJpaEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "listing_id", nullable = false)
    private UUID listingId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected FavoriteJpaEntity() {}

    public FavoriteJpaEntity(UUID userId, UUID listingId, Instant createdAt) {
        this.userId = userId;
        this.listingId = listingId;
        this.createdAt = createdAt;
    }

    public UUID getUserId() { return userId; }
    public UUID getListingId() { return listingId; }
    public Instant getCreatedAt() { return createdAt; }
}
