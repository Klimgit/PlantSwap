package com.plantswap.deals.domain.event;

import com.plantswap.deals.domain.model.DealId;
import com.plantswap.deals.domain.model.ListingId;
import com.plantswap.deals.domain.model.OwnerId;
import com.plantswap.deals.domain.model.RequesterId;

import java.time.Instant;
import java.util.UUID;

public record DealCancelledEvent(
        UUID eventId,
        Instant occurredAt,
        String eventType,
        DealId dealId,
        ListingId listingId,
        OwnerId ownerId,
        RequesterId requesterId,
        UUID cancelledBy
) implements DomainEvent {

    public static DealCancelledEvent of(DealId dealId, ListingId listingId,
                                         OwnerId ownerId, RequesterId requesterId,
                                         UUID cancelledBy) {
        return new DealCancelledEvent(UUID.randomUUID(), Instant.now(), "DEAL_CANCELLED",
                dealId, listingId, ownerId, requesterId, cancelledBy);
    }
}
