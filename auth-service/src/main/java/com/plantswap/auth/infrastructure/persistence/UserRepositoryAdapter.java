package com.plantswap.auth.infrastructure.persistence;

import com.plantswap.auth.domain.model.*;
import com.plantswap.auth.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Адаптер репозитория пользователей.
 * Реализует доменный порт {@link UserRepository}
 * Отвечает за маппинг между доменным агрегатом {@link User} и JPA-сущностью.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository jpa;

    public UserRepositoryAdapter(SpringDataUserRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(User user) {
        jpa.save(toEntity(user));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return jpa.findByUsername(username.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.value());
    }

    @Override
    public boolean existsByUsername(Username username) {
        return jpa.existsByUsername(username.value());
    }

    private UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.id().value(),
                user.username().value(),
                user.email().value(),
                user.passwordHash().value(),
                user.city() != null ? user.city().value() : null,
                user.createdAt(),
                user.updatedAt()
        );
    }

    private User toDomain(UserJpaEntity e) {
        return User.reconstitute(
                UserId.of(e.getId()),
                new Username(e.getUsername()),
                new Email(e.getEmail()),
                new PasswordHash(e.getPasswordHash()),
                City.ofNullable(e.getCity()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
