package com.plantswap.deals.domain.model;

import java.util.UUID;

public record RequesterId(UUID value) {

    public RequesterId {
        if (value == null) throw new IllegalArgumentException("RequesterId не может быть null");
    }

    public static RequesterId of(UUID value) { return new RequesterId(value); }

    @Override
    public String toString() { return value.toString(); }
}
