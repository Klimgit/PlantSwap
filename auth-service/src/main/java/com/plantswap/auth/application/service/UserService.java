package com.plantswap.auth.application.service;

import com.plantswap.auth.application.command.LoginCommand;
import com.plantswap.auth.application.command.RegisterUserCommand;
import com.plantswap.auth.application.port.in.*;
import com.plantswap.auth.application.port.out.*;
import com.plantswap.auth.application.result.TokenPair;
import com.plantswap.auth.application.result.UserProfile;
import com.plantswap.auth.domain.model.*;
import com.plantswap.auth.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Application-сервис для контекста Identity.
 * Оркеструет доменные объекты и порты — не содержит бизнес-логики сам по себе.
 */
@Service
@Transactional
public class UserService implements
        RegisterUserUseCase,
        LoginUseCase,
        RefreshTokenUseCase,
        LogoutUseCase,
        GetUserProfileUseCase {

    private static final long REFRESH_TOKEN_TTL_DAYS = 30;

    private final UserRepository userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final EventPublisherPort eventPublisher;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepositoryPort refreshTokenRepository,
                       PasswordEncoderPort passwordEncoder,
                       TokenGeneratorPort tokenGenerator,
                       EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UserProfile register(RegisterUserCommand command) {
        Email email = new Email(command.email());
        Username username = new Username(command.username());

        if (userRepository.existsByEmail(email))
            throw UserAlreadyExistsException.withEmail(email.value());
        if (userRepository.existsByUsername(username))
            throw UserAlreadyExistsException.withUsername(username.value());

        PasswordHash hash = new PasswordHash(passwordEncoder.encode(command.rawPassword()));
        City city = City.ofNullable(command.city());

        User user = User.register(username, email, hash, city);
        userRepository.save(user);

        user.pullDomainEvents().forEach(eventPublisher::publish);

        return toProfile(user);
    }

    @Override
    public TokenPair login(LoginCommand command) {
        Email email = new Email(command.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.byEmail(email.value()));

        if (!passwordEncoder.matches(command.rawPassword(), user.passwordHash().value()))
            throw new InvalidCredentialsException("Неверный email или пароль");

        return issueTokenPair(user);
    }

    @Override
    public TokenPair refresh(String rawRefreshToken) {
        String tokenHash = passwordEncoder.encode(rawRefreshToken);

        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(t -> !t.isExpiredOrRevoked())
                .orElseThrow(() -> new InvalidTokenException("Refresh-токен недействителен или истёк"));

        stored.revoke();
        refreshTokenRepository.save(stored);

        User user = userRepository.findById(stored.userId())
                .orElseThrow(() -> UserNotFoundException.byId(stored.userId().toString()));

        return issueTokenPair(user);
    }

    @Override
    public void logout(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(UserId.of(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getProfile(UUID userId) {
        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> UserNotFoundException.byId(userId.toString()));
        return toProfile(user);
    }

    private TokenPair issueTokenPair(User user) {
        String accessToken = tokenGenerator.generateAccessToken(
                user.id(), user.email().value(), user.username().value());

        String rawRefreshToken = tokenGenerator.generateRawRefreshToken();
        String tokenHash = passwordEncoder.encode(rawRefreshToken);
        Instant expiresAt = Instant.now().plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS);

        RefreshToken refreshToken = RefreshToken.create(user.id(), tokenHash, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return new TokenPair(accessToken, rawRefreshToken);
    }

    private UserProfile toProfile(User user) {
        return new UserProfile(
                user.id().value(),
                user.username().value(),
                user.email().value(),
                user.city() != null ? user.city().value() : null,
                user.createdAt()
        );
    }
}
