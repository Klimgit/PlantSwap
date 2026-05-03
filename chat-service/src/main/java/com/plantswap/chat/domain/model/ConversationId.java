package com.plantswap.chat.domain.model;

import java.util.UUID;

/** Идентификатор беседы — равен ID сделки. */
public record ConversationId(UUID value) {
    public ConversationId {
        if (value == null) throw new IllegalArgumentException("ConversationId не может быть null");
    }
    public static ConversationId of(UUID value) { return new ConversationId(value); }
    @Override public String toString() { return value.toString(); }
}
