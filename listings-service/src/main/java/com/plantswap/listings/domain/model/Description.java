package com.plantswap.listings.domain.model;

public record Description(String value) {

    private static final int MAX_LENGTH = 3000;

    public Description {
        if (value != null) {
            value = value.strip();
            if (value.length() > MAX_LENGTH)
                throw new IllegalArgumentException(
                        "Описание не должно превышать %d символов".formatted(MAX_LENGTH));
            if (value.isBlank()) value = null;
        }
    }

    public static Description ofNullable(String value) {
        return (value == null || value.isBlank()) ? new Description(null) : new Description(value);
    }

    public boolean isEmpty() { return value == null; }

    @Override
    public String toString() { return value != null ? value : ""; }
}
