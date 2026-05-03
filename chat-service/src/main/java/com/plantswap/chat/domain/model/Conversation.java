package com.plantswap.chat.domain.model;

import java.time.Instant;

/**
 * Агрегат Conversation — чат одной сделки.
 *
 * Создаётся лениво при отправке первого сообщения.
 * Идентификатор беседы совпадает с ID сделки (1:1).
 * Хранит только метаданные.
 */
public class Conversation {

    private final ConversationId id;      // == dealId
    private final ParticipantId ownerId;
    private final ParticipantId requesterId;
    private final Instant createdAt;

    private Conversation(ConversationId id, ParticipantId ownerId,
                         ParticipantId requesterId, Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.requesterId = requesterId;
        this.createdAt = createdAt;
    }

    public static Conversation create(ConversationId id,
                                       ParticipantId ownerId,
                                       ParticipantId requesterId) {
        return new Conversation(id, ownerId, requesterId, Instant.now());
    }

    public static Conversation reconstitute(ConversationId id, ParticipantId ownerId,
                                             ParticipantId requesterId, Instant createdAt) {
        return new Conversation(id, ownerId, requesterId, createdAt);
    }

    public boolean isParticipant(ParticipantId userId) {
        return ownerId.equals(userId) || requesterId.equals(userId);
    }

    public ConversationId id() { return id; }
    public ParticipantId ownerId() { return ownerId; }
    public ParticipantId requesterId() { return requesterId; }
    public Instant createdAt() { return createdAt; }
}
