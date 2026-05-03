package com.plantswap.chat.domain.model;

import java.time.Instant;


public class Message {

    private final MessageId id;
    private final ConversationId conversationId;
    private final ParticipantId senderId;
    private final MessageContent content;
    private final Instant sentAt;

    private Message(MessageId id, ConversationId conversationId,
                    ParticipantId senderId, MessageContent content, Instant sentAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public static Message create(ConversationId conversationId,
                                  ParticipantId senderId, MessageContent content) {
        return new Message(MessageId.generate(), conversationId, senderId, content, Instant.now());
    }

    public static Message reconstitute(MessageId id, ConversationId conversationId,
                                        ParticipantId senderId, MessageContent content,
                                        Instant sentAt) {
        return new Message(id, conversationId, senderId, content, sentAt);
    }

    public MessageId id() { return id; }
    public ConversationId conversationId() { return conversationId; }
    public ParticipantId senderId() { return senderId; }
    public MessageContent content() { return content; }
    public Instant sentAt() { return sentAt; }
}
