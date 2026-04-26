package com.plantswap.deals.domain.event;

import com.plantswap.deals.domain.model.DealId;
import com.plantswap.deals.domain.model.ListingId;
import com.plantswap.deals.domain.model.OwnerId;
import com.plantswap.deals.domain.model.RequesterId;

import java.time.Instant;
import java.util.UUID;

public record DealAcceptedEvent(
        UUID eventId,
        Instant occurredAt,
        DealId dealId,
        ListingId listingId,
        OwnerId ownerId,
        RequesterId requesterId
) implements DomainEvent {

    @Override
    public String eventType() { return "DEAL_ACCEPTED"; }

    public static DealAcceptedEvent of(DealId dealId, ListingId listingId,
                                        OwnerId ownerId, RequesterId requesterId) {
        return new DealAcceptedEvent(UUID.randomUUID(), Instant.now(),
                dealId, listingId, ownerId, requesterId);
    }
}
