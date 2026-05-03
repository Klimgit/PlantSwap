package com.plantswap.chat.domain.model;

import java.util.UUID;

public record ParticipantId(UUID value) {
    public ParticipantId {
        if (value == null) throw new IllegalArgumentException("ParticipantId не может быть null");
    }
    public static ParticipantId of(UUID value) { return new ParticipantId(value); }
    @Override public String toString() { return value.toString(); }
}
