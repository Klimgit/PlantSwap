package com.plantswap.chat.domain.repository;

import com.plantswap.chat.domain.model.ConversationId;
import com.plantswap.chat.domain.model.Message;

import java.util.List;

public interface MessageRepository {
    void save(Message message);
    List<Message> findByConversationId(ConversationId id, int page, int size);
    long countByConversationId(ConversationId id);
}
