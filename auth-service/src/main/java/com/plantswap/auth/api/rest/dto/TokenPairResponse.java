package com.plantswap.auth.api.rest.dto;

public record TokenPairResponse(
        String accessToken,
        String refreshToken
) {}
