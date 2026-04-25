package com.plantswap.listings.domain.model;

public class ListingAlreadyClosedException extends RuntimeException {

    public ListingAlreadyClosedException(ListingId id) {
        super("Объявление уже закрыто: " + id);
    }

    public ListingAlreadyClosedException(String message) {
        super(message);
    }
}
