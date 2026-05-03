package com.plantswap.chat.application.service;

import com.plantswap.chat.application.command.SendMessageCommand;
import com.plantswap.chat.application.port.in.GetHistoryUseCase;
import com.plantswap.chat.application.port.in.SendMessageUseCase;
import com.plantswap.chat.application.port.out.DealParticipantCheckPort;
import com.plantswap.chat.application.port.out.DealParticipantCheckPort.DealParticipants;
import com.plantswap.chat.application.port.out.MessageBroadcastPort;
import com.plantswap.chat.application.result.MessageDto;
import com.plantswap.chat.application.result.PageDto;
import com.plantswap.chat.domain.model.*;
import com.plantswap.chat.domain.repository.ConversationRepository;
import com.plantswap.chat.domain.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application-сервис чата.
 */
@Service
@Transactional
public class ChatService implements SendMessageUseCase, GetHistoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final DealParticipantCheckPort dealParticipantCheck;
    private final MessageBroadcastPort messageBroadcast;

    public ChatService(ConversationRepository conversationRepository,
                       MessageRepository messageRepository,
                       DealParticipantCheckPort dealParticipantCheck,
                       MessageBroadcastPort messageBroadcast) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.dealParticipantCheck = dealParticipantCheck;
        this.messageBroadcast = messageBroadcast;
    }

    @Override
    public MessageDto send(SendMessageCommand cmd) {
        ConversationId conversationId = ConversationId.of(cmd.dealId());
        ParticipantId senderId = ParticipantId.of(cmd.senderId());

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseGet(() -> createConversation(conversationId, cmd.senderId()));

        if (!conversation.isParticipant(senderId))
            throw new ChatAccessDeniedException(conversationId, cmd.senderId());

        Message message = Message.create(conversationId, senderId,
                new MessageContent(cmd.content()));
        messageRepository.save(message);

        MessageDto dto = toDto(message);
        messageBroadcast.broadcast(cmd.dealId(), dto);

        log.debug("Сообщение отправлено: conversationId={}, senderId={}", conversationId, senderId);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<MessageDto> getHistory(UUID dealId, UUID requesterId, int page, int size) {
        ConversationId conversationId = ConversationId.of(dealId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(conversationId));

        if (!conversation.isParticipant(ParticipantId.of(requesterId)))
            throw new ChatAccessDeniedException(conversationId, requesterId);

        List<MessageDto> messages = messageRepository
                .findByConversationId(conversationId, page, size)
                .stream().map(this::toDto).toList();
        long total = messageRepository.countByConversationId(conversationId);

        return PageDto.of(messages, page, size, total);
    }

    // ── Вспомогательные методы ─────────────────────────────────────────────

    private Conversation createConversation(ConversationId conversationId, UUID senderId) {
        DealParticipants participants = dealParticipantCheck.check(
                conversationId.value(), senderId);

        if (!participants.isParticipant())
            throw new ChatAccessDeniedException(conversationId, senderId);

        Conversation conversation = Conversation.create(
                conversationId,
                ParticipantId.of(participants.ownerId()),
                ParticipantId.of(participants.requesterId())
        );
        conversationRepository.save(conversation);
        log.info("Беседа создана: dealId={}", conversationId);
        return conversation;
    }

    private MessageDto toDto(Message m) {
        return new MessageDto(
                m.id().value(), m.conversationId().value(),
                m.senderId().value(), m.content().value(), m.sentAt()
        );
    }
}
