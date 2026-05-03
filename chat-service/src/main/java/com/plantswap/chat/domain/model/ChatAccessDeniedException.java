package com.plantswap.chat.domain.model;

import java.util.UUID;

public class ChatAccessDeniedException extends RuntimeException {
    public ChatAccessDeniedException(ConversationId conversationId, UUID userId) {
        super("Пользователь %s не является участником беседы %s"
                .formatted(userId, conversationId));
    }
}
