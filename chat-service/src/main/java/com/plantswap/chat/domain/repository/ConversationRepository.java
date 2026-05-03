package com.plantswap.chat.domain.repository;

import com.plantswap.chat.domain.model.Conversation;
import com.plantswap.chat.domain.model.ConversationId;

import java.util.Optional;

/** Порт репозитория бесед. */
public interface ConversationRepository {
    void save(Conversation conversation);
    Optional<Conversation> findById(ConversationId id);
}
