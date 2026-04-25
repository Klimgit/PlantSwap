package com.plantswap.auth.domain.event;

import com.plantswap.auth.domain.model.UserId;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        Instant occurredAt,
        UserId userId,
        String username,
        String email
) implements DomainEvent {

    public static UserRegisteredEvent of(UserId userId, String username, String email) {
        return new UserRegisteredEvent(
                UUID.randomUUID(),
                Instant.now(),
                userId,
                username,
                email
        );
    }

    @Override
    public String eventType() {
        return "USER_REGISTERED";
    }
}
