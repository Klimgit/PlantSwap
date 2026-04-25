package com.plantswap.listings.domain.model;

public class ListingNotFoundException extends RuntimeException {

    public ListingNotFoundException(String message) {
        super(message);
    }

    public static ListingNotFoundException byId(ListingId id) {
        return new ListingNotFoundException("Объявление не найдено: " + id);
    }
}
