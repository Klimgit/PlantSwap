package com.plantswap.listings.domain.model;

public record City(String value) {

    private static final int MAX_LENGTH = 100;

    public City {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Название города не может быть пустым");
        value = value.strip();
        if (value.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                    "Название города не должно превышать %d символов".formatted(MAX_LENGTH));
    }

    public static City ofNullable(String value) {
        return (value == null || value.isBlank()) ? null : new City(value);
    }

    @Override
    public String toString() { return value; }
}
