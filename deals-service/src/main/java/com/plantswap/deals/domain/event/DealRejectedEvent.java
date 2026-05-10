package com.plantswap.deals.domain.event;

import com.plantswap.deals.domain.model.DealId;
import com.plantswap.deals.domain.model.ListingId;
import com.plantswap.deals.domain.model.OwnerId;
import com.plantswap.deals.domain.model.RequesterId;

import java.time.Instant;
import java.util.UUID;

public record DealRejectedEvent(
        UUID eventId,
        Instant occurredAt,
        String eventType,
        DealId dealId,
        ListingId listingId,
        OwnerId ownerId,
        RequesterId requesterId
) implements DomainEvent {

    public static DealRejectedEvent of(DealId dealId, ListingId listingId,
                                        OwnerId ownerId, RequesterId requesterId) {
        return new DealRejectedEvent(UUID.randomUUID(), Instant.now(), "DEAL_REJECTED",
                dealId, listingId, ownerId, requesterId);
    }
}
