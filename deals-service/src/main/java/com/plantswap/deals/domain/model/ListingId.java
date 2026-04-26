package com.plantswap.deals.domain.model;

import java.util.UUID;

public record ListingId(UUID value) {

    public ListingId {
        if (value == null) throw new IllegalArgumentException("ListingId не может быть null");
    }

    public static ListingId of(UUID value) { return new ListingId(value); }

    @Override
    public String toString() { return value.toString(); }
}
