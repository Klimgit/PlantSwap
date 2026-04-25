package com.plantswap.auth.domain.model;

public record Username(String value) {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;
    private static final String ALLOWED = "^[a-zA-Z0-9_.-]+$";

    public Username {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        String trimmed = value.strip();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                    "Имя пользователя должно быть от %d до %d символов".formatted(MIN_LENGTH, MAX_LENGTH));
        if (!trimmed.matches(ALLOWED))
            throw new IllegalArgumentException(
                    "Имя пользователя может содержать только буквы, цифры, _, . и -");
        value = trimmed;
    }

    @Override
    public String toString() {
        return value;
    }
}
