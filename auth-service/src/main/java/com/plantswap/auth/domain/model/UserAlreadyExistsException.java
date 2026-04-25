package com.plantswap.auth.domain.model;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException withEmail(String email) {
        return new UserAlreadyExistsException("Пользователь с email '%s' уже существует".formatted(email));
    }

    public static UserAlreadyExistsException withUsername(String username) {
        return new UserAlreadyExistsException("Пользователь с именем '%s' уже существует".formatted(username));
    }
}
