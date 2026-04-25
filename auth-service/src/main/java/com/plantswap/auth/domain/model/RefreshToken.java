package com.plantswap.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {

    private final UUID id;
    private final UserId userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private boolean revoked;
    private final Instant createdAt;

    private RefreshToken(UUID id, UserId userId, String tokenHash,
                         Instant expiresAt, boolean revoked, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    /** Создаёт новый refresh-токен. */
    public static RefreshToken create(UserId userId, String tokenHash, Instant expiresAt) {
        return new RefreshToken(UUID.randomUUID(), userId, tokenHash, expiresAt, false, Instant.now());
    }

    /** Восстановление из базы данных. */
    public static RefreshToken reconstitute(UUID id, UserId userId, String tokenHash,
                                            Instant expiresAt, boolean revoked, Instant createdAt) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revoked, createdAt);
    }

    /** Истёк ли токен или был отозван. */
    public boolean isExpiredOrRevoked() {
        return revoked || Instant.now().isAfter(expiresAt);
    }

    public void revoke() {
        this.revoked = true;
    }

    public UUID id() { return id; }
    public UserId userId() { return userId; }
    public String tokenHash() { return tokenHash; }
    public Instant expiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public Instant createdAt() { return createdAt; }
}
