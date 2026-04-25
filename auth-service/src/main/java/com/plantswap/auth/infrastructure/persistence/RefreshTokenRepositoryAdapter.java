package com.plantswap.auth.infrastructure.persistence;

import com.plantswap.auth.application.port.out.RefreshTokenRepositoryPort;
import com.plantswap.auth.domain.model.RefreshToken;
import com.plantswap.auth.domain.model.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Адаптер репозитория refresh-токенов.
 * Реализует порт {@link RefreshTokenRepositoryPort} через Spring Data JPA.
 */
@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final SpringDataRefreshTokenRepository jpa;

    public RefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(RefreshToken token) {
        jpa.save(toEntity(token));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpa.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void revokeAllByUserId(UserId userId) {
        jpa.revokeAllByUserId(userId.value());
    }

    private RefreshTokenJpaEntity toEntity(RefreshToken t) {
        return new RefreshTokenJpaEntity(
                t.id(), t.userId().value(), t.tokenHash(),
                t.expiresAt(), t.isRevoked(), t.createdAt()
        );
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity e) {
        return RefreshToken.reconstitute(
                e.getId(), UserId.of(e.getUserId()),
                e.getTokenHash(), e.getExpiresAt(), e.isRevoked(), e.getCreatedAt()
        );
    }
}
