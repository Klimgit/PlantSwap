package com.plantswap.auth.application.port.out;

import com.plantswap.auth.domain.model.RefreshToken;
import com.plantswap.auth.domain.model.UserId;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    void save(RefreshToken token);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void revokeAllByUserId(UserId userId);
}
