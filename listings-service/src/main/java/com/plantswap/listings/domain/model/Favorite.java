package com.plantswap.listings.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Favorite(
        UUID userId,
        ListingId listingId,
        Instant createdAt
) {
    public Favorite {
        if (userId == null) throw new IllegalArgumentException("userId не может быть null");
        if (listingId == null) throw new IllegalArgumentException("listingId не может быть null");
        if (createdAt == null) createdAt = Instant.now();
    }

    public static Favorite of(UUID userId, ListingId listingId) {
        return new Favorite(userId, listingId, Instant.now());
    }
}
