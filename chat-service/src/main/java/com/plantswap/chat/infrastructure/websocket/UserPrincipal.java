package com.plantswap.chat.infrastructure.websocket;

import java.security.Principal;

/**
 * Реализация Principal для WebSocket-сессии.
 */
public record UserPrincipal(String name) implements Principal {
    @Override
    public String getName() { return name; }
}
