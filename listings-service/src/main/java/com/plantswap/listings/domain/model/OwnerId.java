package com.plantswap.listings.domain.model;

import java.util.UUID;

public record OwnerId(UUID value) {

    public OwnerId {
        if (value == null) throw new IllegalArgumentException("OwnerId не может быть null");
    }

    public static OwnerId of(UUID value) { return new OwnerId(value); }
    public static OwnerId of(String value) { return new OwnerId(UUID.fromString(value)); }

    @Override
    public String toString() { return value.toString(); }
}
