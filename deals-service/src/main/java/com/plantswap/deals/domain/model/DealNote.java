package com.plantswap.deals.domain.model;

public record DealNote(String value) {

    private static final int MAX_LENGTH = 500;

    public DealNote {
        if (value != null && value.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                    "Сопроводительное сообщение не может превышать %d символов".formatted(MAX_LENGTH));
    }

    public static DealNote ofNullable(String value) {
        return value == null || value.isBlank() ? new DealNote(null) : new DealNote(value.strip());
    }

    public boolean isEmpty() { return value == null || value.isBlank(); }
}
