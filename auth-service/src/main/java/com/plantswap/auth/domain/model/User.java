package com.plantswap.auth.domain.model;

import com.plantswap.auth.domain.event.UserRegisteredEvent;

import java.time.Instant;

/**
 * Агрегат User — корневая сущность контекста Identity.
 *
 * Все бизнес-инварианты создания пользователя и обновления профиля живут здесь..
 */
public class User extends AggregateRoot {

    private final UserId id;
    private Username username;
    private Email email;
    private PasswordHash passwordHash;
    private City city;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(UserId id, Username username, Email email,
                 PasswordHash passwordHash, City city, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.city = city;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public static User register(Username username, Email email, PasswordHash passwordHash, City city) {
        UserId id = UserId.generate();
        Instant now = Instant.now();
        User user = new User(id, username, email, passwordHash, city, now);
        user.registerEvent(UserRegisteredEvent.of(id, username.value(), email.value()));
        return user;
    }

    public static User reconstitute(UserId id, Username username, Email email,
                                    PasswordHash passwordHash, City city,
                                    Instant createdAt, Instant updatedAt) {
        User user = new User(id, username, email, passwordHash, city, createdAt);
        user.updatedAt = updatedAt;
        return user;
    }

    public void updateProfile(Username newUsername, City newCity) {
        this.username = newUsername;
        this.city = newCity;
        this.updatedAt = Instant.now();
    }

    public UserId id() { return id; }
    public Username username() { return username; }
    public Email email() { return email; }
    public PasswordHash passwordHash() { return passwordHash; }
    public City city() { return city; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
