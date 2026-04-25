package com.plantswap.auth.domain.model;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Email не может быть пустым");
        if (!PATTERN.matcher(value).matches())
            throw new IllegalArgumentException("Некорректный формат email: " + value);
        value = value.toLowerCase().strip();
    }

    @Override
    public String toString() {
        return value;
    }
}
