package com.plantswap.auth.application.command;

public record LoginCommand(
        String email,
        String rawPassword
) {}
