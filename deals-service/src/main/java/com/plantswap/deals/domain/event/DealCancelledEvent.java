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
        DealId dealId,
        ListingId listingId,
        OwnerId ownerId,
        RequesterId requesterId,
        UUID cancelledBy
) implements DomainEvent {

    @Override
    public String eventType() { return "DEAL_CANCELLED"; }

    public static DealCancelledEvent of(DealId dealId, ListingId listingId,
                                         OwnerId ownerId, RequesterId requesterId,
                                         UUID cancelledBy) {
        return new DealCancelledEvent(UUID.randomUUID(), Instant.now(),
                dealId, listingId, ownerId, requesterId, cancelledBy);
    }
}
