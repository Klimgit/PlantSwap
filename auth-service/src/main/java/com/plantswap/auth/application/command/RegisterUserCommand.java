package com.plantswap.auth.application.command;

public record RegisterUserCommand(
        String username,
        String email,
        String rawPassword,
        String city
) {}
