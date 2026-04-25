package com.plantswap.listings.domain.model;

import java.util.UUID;

public record PhotoId(UUID value) {

    public PhotoId {
        if (value == null) throw new IllegalArgumentException("PhotoId не может быть null");
    }

    public static PhotoId generate() { return new PhotoId(UUID.randomUUID()); }
    public static PhotoId of(UUID value) { return new PhotoId(value); }

    @Override
    public String toString() { return value.toString(); }
}
