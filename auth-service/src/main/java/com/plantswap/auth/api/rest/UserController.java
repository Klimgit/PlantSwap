package com.plantswap.auth.api.rest;

import com.plantswap.auth.api.rest.dto.UserProfileResponse;
import com.plantswap.auth.application.port.in.GetUserProfileUseCase;
import com.plantswap.auth.application.result.UserProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** Контроллер профилей пользователей. */
@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Получение профилей пользователей")
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;

    public UserController(GetUserProfileUseCase getUserProfileUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить публичный профиль пользователя по ID")
    public UserProfileResponse getProfile(@PathVariable UUID id) {
        UserProfile profile = getUserProfileUseCase.getProfile(id);
        return toResponse(profile);
    }

    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя")
    public UserProfileResponse getMyProfile(@RequestHeader("X-User-Id") UUID userId) {
        UserProfile profile = getUserProfileUseCase.getProfile(userId);
        return toResponse(profile);
    }

    private UserProfileResponse toResponse(UserProfile p) {
        return new UserProfileResponse(p.id(), p.username(), p.email(), p.city(), p.createdAt());
    }
}
