package com.plantswap.auth.domain.model;

public record PasswordHash(String value) {

    public PasswordHash {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("PasswordHash не может быть пустым");
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}
