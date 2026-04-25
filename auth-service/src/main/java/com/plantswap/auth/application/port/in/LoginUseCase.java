package com.plantswap.auth.application.port.in;

import com.plantswap.auth.application.command.LoginCommand;
import com.plantswap.auth.application.result.TokenPair;

public interface LoginUseCase {
    TokenPair login(LoginCommand command);
}
