package com.plantswap.chat.infrastructure.websocket;

import com.plantswap.chat.application.command.SendMessageCommand;
import com.plantswap.chat.application.port.in.SendMessageUseCase;
import com.plantswap.chat.application.result.MessageDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

/**
 * STOMP-контроллер для приёма входящих сообщений.
 */
@Controller
public class StompMessageController {

    private final SendMessageUseCase sendMessage;

    public StompMessageController(SendMessageUseCase sendMessage) {
        this.sendMessage = sendMessage;
    }

    @MessageMapping("/deal/{dealId}")
    public void handle(@DestinationVariable UUID dealId,
                       @Payload IncomingMessage payload,
                       Principal principal) {
        if (principal == null)
            throw new IllegalStateException("Пользователь не аутентифицирован в WebSocket-сессии");

        UUID senderId = UUID.fromString(principal.getName());
        sendMessage.send(new SendMessageCommand(dealId, senderId, payload.content()));
    }

    /** DTO входящего STOMP-фрейма. */
    public record IncomingMessage(String content) {}
}
