package com.plantswap.listings.domain.model;

import java.util.UUID;

public record ListingId(UUID value) {

    public ListingId {
        if (value == null) throw new IllegalArgumentException("ListingId не может быть null");
    }

    public static ListingId generate() { return new ListingId(UUID.randomUUID()); }
    public static ListingId of(UUID value) { return new ListingId(value); }
    public static ListingId of(String value) { return new ListingId(UUID.fromString(value)); }

    @Override
    public String toString() { return value.toString(); }
}
