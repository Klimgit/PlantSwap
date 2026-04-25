package com.plantswap.auth.application.port.in;

import com.plantswap.auth.application.command.RegisterUserCommand;
import com.plantswap.auth.application.result.UserProfile;

public interface RegisterUserUseCase {
    UserProfile register(RegisterUserCommand command);
}
