package com.plantswap.listings.domain.model;

public class ListingAccessDeniedException extends RuntimeException {

    public ListingAccessDeniedException(ListingId listingId, OwnerId requesterId) {
        super("Пользователь %s не является владельцем объявления %s"
                .formatted(requesterId, listingId));
    }
}
