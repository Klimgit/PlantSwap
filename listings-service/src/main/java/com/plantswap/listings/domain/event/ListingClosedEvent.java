package com.plantswap.listings.domain.event;

import com.plantswap.listings.domain.model.ListingId;
import com.plantswap.listings.domain.model.OwnerId;

import java.time.Instant;
import java.util.UUID;

public record ListingClosedEvent(
        UUID eventId,
        Instant occurredAt,
        ListingId listingId,
        OwnerId ownerId
) implements DomainEvent {

    public static ListingClosedEvent of(ListingId listingId, OwnerId ownerId) {
        return new ListingClosedEvent(UUID.randomUUID(), Instant.now(), listingId, ownerId);
    }

    @Override
    public String eventType() { return "LISTING_CLOSED"; }
}
