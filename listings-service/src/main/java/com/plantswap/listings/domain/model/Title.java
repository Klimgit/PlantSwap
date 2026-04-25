package com.plantswap.listings.domain.model;

public record Title(String value) {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 120;

    public Title {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Заголовок объявления не может быть пустым");
        value = value.strip();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                    "Заголовок должен быть от %d до %d символов".formatted(MIN_LENGTH, MAX_LENGTH));
    }

    @Override
    public String toString() { return value; }
}
