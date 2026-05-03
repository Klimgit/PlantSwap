package com.plantswap.chat.domain.model;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException(ConversationId id) {
        super("Беседа не найдена: " + id);
    }
}
