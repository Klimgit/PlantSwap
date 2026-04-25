package com.plantswap.auth.api.rest;

import com.plantswap.auth.api.rest.dto.*;
import com.plantswap.auth.application.command.LoginCommand;
import com.plantswap.auth.application.command.RegisterUserCommand;
import com.plantswap.auth.application.port.in.*;
import com.plantswap.auth.application.result.TokenPair;
import com.plantswap.auth.application.result.UserProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Контроллер аутентификации.
 * Обрабатывает регистрацию, вход, обновление токена и выход из системы.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "Регистрация, вход, управление токенами")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация нового пользователя")
    public UserProfileResponse register(@Valid @RequestBody RegisterRequest request) {
        UserProfile profile = registerUserUseCase.register(new RegisterUserCommand(
                request.username(),
                request.email(),
                request.password(),
                request.city()
        ));
        return toResponse(profile);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему, возвращает пару access + refresh токенов")
    public TokenPairResponse login(@Valid @RequestBody LoginRequest request) {
        TokenPair pair = loginUseCase.login(new LoginCommand(request.email(), request.password()));
        return new TokenPairResponse(pair.accessToken(), pair.refreshToken());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление access-токена по refresh-токену")
    public TokenPairResponse refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair pair = refreshTokenUseCase.refresh(request.refreshToken());
        return new TokenPairResponse(pair.accessToken(), pair.refreshToken());
    }


    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Выход из системы (отзыв всех refresh-токенов)")
    public void logout(@RequestHeader("X-User-Id") UUID userId) {
        logoutUseCase.logout(userId);
    }

    private UserProfileResponse toResponse(UserProfile p) {
        return new UserProfileResponse(p.id(), p.username(), p.email(), p.city(), p.createdAt());
    }
}
