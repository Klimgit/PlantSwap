package com.plantswap.auth.domain.model;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException byId(String id) {
        return new UserNotFoundException("Пользователь не найден: " + id);
    }

    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("Пользователь с email '%s' не найден".formatted(email));
    }
}
