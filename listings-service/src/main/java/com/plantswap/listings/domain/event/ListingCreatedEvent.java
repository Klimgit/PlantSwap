package com.plantswap.listings.domain.event;

import com.plantswap.listings.domain.model.ListingId;
import com.plantswap.listings.domain.model.ListingType;
import com.plantswap.listings.domain.model.OwnerId;

import java.time.Instant;
import java.util.UUID;

public record ListingCreatedEvent(
        UUID eventId,
        Instant occurredAt,
        ListingId listingId,
        OwnerId ownerId,
        ListingType type
) implements DomainEvent {

    public static ListingCreatedEvent of(ListingId listingId, OwnerId ownerId, ListingType type) {
        return new ListingCreatedEvent(UUID.randomUUID(), Instant.now(), listingId, ownerId, type);
    }

    @Override
    public String eventType() { return "LISTING_CREATED"; }
}
