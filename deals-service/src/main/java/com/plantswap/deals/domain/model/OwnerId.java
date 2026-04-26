package com.plantswap.deals.domain.model;

import java.util.UUID;

public record OwnerId(UUID value) {

    public OwnerId {
        if (value == null) throw new IllegalArgumentException("OwnerId не может быть null");
    }

    public static OwnerId of(UUID value) { return new OwnerId(value); }

    @Override
    public String toString() { return value.toString(); }
}
