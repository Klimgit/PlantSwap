package com.plantswap.chat.domain.model;

public record MessageContent(String value) {

    private static final int MAX_LENGTH = 2000;

    public MessageContent {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Текст сообщения не может быть пустым");
        if (value.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                    "Сообщение не может превышать %d символов".formatted(MAX_LENGTH));
        value = value.strip();
    }
}
