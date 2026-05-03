package com.plantswap.chat.infrastructure.persistence;

import com.plantswap.chat.domain.model.*;
import com.plantswap.chat.domain.repository.ConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Адаптер репозитория бесед. */
@Repository
public class ConversationRepositoryAdapter implements ConversationRepository {

    private final SpringDataConversationRepository jpa;

    public ConversationRepositoryAdapter(SpringDataConversationRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Conversation c) {
        jpa.save(new ConversationJpaEntity(
                c.id().value(), c.ownerId().value(),
                c.requesterId().value(), c.createdAt()));
    }

    @Override
    public Optional<Conversation> findById(ConversationId id) {
        return jpa.findById(id.value()).map(e -> Conversation.reconstitute(
                ConversationId.of(e.getId()),
                ParticipantId.of(e.getOwnerId()),
                ParticipantId.of(e.getRequesterId()),
                e.getCreatedAt()));
    }
}
