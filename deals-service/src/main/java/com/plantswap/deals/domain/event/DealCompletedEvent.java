package com.plantswap.deals.domain.event;

import com.plantswap.deals.domain.model.DealId;
import com.plantswap.deals.domain.model.ListingId;
import com.plantswap.deals.domain.model.OwnerId;
import com.plantswap.deals.domain.model.RequesterId;

import java.time.Instant;
import java.util.UUID;

public record DealCompletedEvent(
        UUID eventId,
        Instant occurredAt,
        String eventType,
        DealId dealId,
        ListingId listingId,
        OwnerId ownerId,
        RequesterId requesterId
) implements DomainEvent {

    public static DealCompletedEvent of(DealId dealId, ListingId listingId,
                                         OwnerId ownerId, RequesterId requesterId) {
        return new DealCompletedEvent(UUID.randomUUID(), Instant.now(), "DEAL_COMPLETED",
                dealId, listingId, ownerId, requesterId);
    }
}
