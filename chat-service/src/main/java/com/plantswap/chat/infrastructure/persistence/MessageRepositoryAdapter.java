package com.plantswap.chat.infrastructure.persistence;

import com.plantswap.chat.domain.model.*;
import com.plantswap.chat.domain.repository.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepositoryAdapter implements MessageRepository {

    private final SpringDataMessageRepository jpa;

    public MessageRepositoryAdapter(SpringDataMessageRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Message m) {
        jpa.save(new MessageJpaEntity(
                m.id().value(), m.conversationId().value(),
                m.senderId().value(), m.content().value(), m.sentAt()));
    }

    @Override
    public List<Message> findByConversationId(ConversationId id, int page, int size) {
        return jpa.findByConversationIdOrderBySentAtDesc(
                        id.value(), PageRequest.of(page, size))
                .stream()
                .map(e -> Message.reconstitute(
                        MessageId.of(e.getId()), ConversationId.of(e.getConversationId()),
                        ParticipantId.of(e.getSenderId()),
                        new MessageContent(e.getContent()), e.getSentAt()))
                .toList();
    }

    @Override
    public long countByConversationId(ConversationId id) {
        return jpa.countByConversationId(id.value());
    }
}
