package com.plantswap.chat.domain.model;

import java.util.UUID;

public record MessageId(UUID value) {
    public MessageId {
        if (value == null) throw new IllegalArgumentException("MessageId не может быть null");
    }
    public static MessageId of(UUID value) { return new MessageId(value); }
    public static MessageId generate() { return new MessageId(UUID.randomUUID()); }
    @Override public String toString() { return value.toString(); }
}
