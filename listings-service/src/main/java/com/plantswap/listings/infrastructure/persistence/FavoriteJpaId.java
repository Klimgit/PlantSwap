package com.plantswap.listings.infrastructure.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class FavoriteJpaId implements Serializable {
    private UUID userId;
    private UUID listingId;

    public FavoriteJpaId() {}
    public FavoriteJpaId(UUID userId, UUID listingId) {
        this.userId = userId;
        this.listingId = listingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteJpaId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(listingId, that.listingId);
    }

    @Override
    public int hashCode() { return Objects.hash(userId, listingId); }
}
