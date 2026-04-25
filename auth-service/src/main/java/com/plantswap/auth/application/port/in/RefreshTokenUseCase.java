package com.plantswap.auth.application.port.in;

import com.plantswap.auth.application.result.TokenPair;

public interface RefreshTokenUseCase {
    TokenPair refresh(String rawRefreshToken);
}
