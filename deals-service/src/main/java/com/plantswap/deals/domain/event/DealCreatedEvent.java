package com.plantswap.deals.domain.event;

import com.plantswap.deals.domain.model.DealId;
import com.plantswap.deals.domain.model.ListingId;
import com.plantswap.deals.domain.model.OwnerId;
import com.plantswap.deals.domain.model.RequesterId;

import java.time.Instant;
import java.util.UUID;

public record DealCreatedEvent(
        UUID eventId,
        Instant occurredAt,
        String eventType,
        DealId dealId,
        ListingId listingId,
        OwnerId ownerId,
        RequesterId requesterId
) implements DomainEvent {

    public static DealCreatedEvent of(DealId dealId, ListingId listingId,
                                       OwnerId ownerId, RequesterId requesterId) {
        return new DealCreatedEvent(UUID.randomUUID(), Instant.now(), "DEAL_CREATED",
                dealId, listingId, ownerId, requesterId);
    }
}
