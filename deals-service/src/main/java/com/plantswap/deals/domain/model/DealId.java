package com.plantswap.deals.domain.model;

import java.util.UUID;

public record DealId(UUID value) {

    public DealId {
        if (value == null) throw new IllegalArgumentException("DealId не может быть null");
    }

    public static DealId of(UUID value) { return new DealId(value); }
    public static DealId generate() { return new DealId(UUID.randomUUID()); }

    @Override
    public String toString() { return value.toString(); }
}
