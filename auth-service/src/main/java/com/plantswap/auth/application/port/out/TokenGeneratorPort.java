package com.plantswap.auth.application.port.out;

import com.plantswap.auth.domain.model.UserId;

/**
 * Порт генерации и валидации JWT-токенов.
 */
public interface TokenGeneratorPort {

    String generateAccessToken(UserId userId, String email, String username);

    String generateRawRefreshToken();

    UserId extractUserId(String accessToken);

    String extractEmail(String accessToken);

    String extractUsername(String accessToken);

    boolean isAccessTokenValid(String accessToken);
}
