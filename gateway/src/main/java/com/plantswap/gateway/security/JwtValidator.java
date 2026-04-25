package com.plantswap.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Валидатор JWT для Gateway.
 */
@Component
public class JwtValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtValidator.class);

    private final SecretKey secretKey;

    public JwtValidator(JwtProperties props) {
        this.secretKey = Keys.hmacShaKeyFor(
                props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public Optional<String> extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException e) {
            log.debug("Невалидный JWT: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Неожиданная ошибка при парсинге JWT", e);
            return Optional.empty();
        }
    }

    public boolean isValid(String token) {
        return extractUserId(token).isPresent();
    }
}
