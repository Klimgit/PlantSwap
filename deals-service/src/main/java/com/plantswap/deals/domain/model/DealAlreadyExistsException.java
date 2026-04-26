package com.plantswap.deals.domain.model;

public class DealAlreadyExistsException extends RuntimeException {

    public DealAlreadyExistsException(ListingId listingId, RequesterId requesterId) {
        super("Активная сделка по объявлению %s от пользователя %s уже существует"
                .formatted(listingId, requesterId));
    }
}
