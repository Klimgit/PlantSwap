package com.plantswap.auth.application.result;

public record TokenPair(
        String accessToken,
        String refreshToken
) {}
