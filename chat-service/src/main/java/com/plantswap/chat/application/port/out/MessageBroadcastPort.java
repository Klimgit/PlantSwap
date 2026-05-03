package com.plantswap.chat.application.port.out;

import com.plantswap.chat.application.result.MessageDto;

import java.util.UUID;

/** Порт рассылки сообщения через STOMP. */
public interface MessageBroadcastPort {
    void broadcast(UUID dealId, MessageDto message);
}
