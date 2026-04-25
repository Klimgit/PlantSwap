package com.plantswap.auth.infrastructure.security;

import com.plantswap.auth.application.port.out.TokenGeneratorPort;
import com.plantswap.auth.domain.model.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Реализация {@link TokenGeneratorPort}.
 */
@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private static final String CLAIM_EMAIL    = "email";
    private static final String CLAIM_USERNAME = "username";

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;

    public JwtTokenGeneratorAdapter(JwtProperties props) {
        this.secretKey = Keys.hmacShaKeyFor(
                props.secret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = props.accessTokenExpirationMs();
    }

    @Override
    public String generateAccessToken(UserId userId, String email, String username) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_USERNAME, username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRawRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public UserId extractUserId(String accessToken) {
        return UserId.of(parseClaims(accessToken).getSubject());
    }

    @Override
    public String extractEmail(String accessToken) {
        return parseClaims(accessToken).get(CLAIM_EMAIL, String.class);
    }

    @Override
    public String extractUsername(String accessToken) {
        return parseClaims(accessToken).get(CLAIM_USERNAME, String.class);
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        try {
            parseClaims(accessToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
