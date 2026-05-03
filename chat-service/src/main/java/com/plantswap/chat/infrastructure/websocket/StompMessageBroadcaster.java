package com.plantswap.chat.infrastructure.websocket;

import com.plantswap.chat.application.port.out.MessageBroadcastPort;
import com.plantswap.chat.application.result.MessageDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Адаптер широковещательной рассылки сообщений через STOMP Simple Broker.
 */
@Component
public class StompMessageBroadcaster implements MessageBroadcastPort {

    private final SimpMessagingTemplate messagingTemplate;

    public StompMessageBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void broadcast(UUID dealId, MessageDto message) {
        messagingTemplate.convertAndSend("/topic/deal/" + dealId, message);
    }
}
