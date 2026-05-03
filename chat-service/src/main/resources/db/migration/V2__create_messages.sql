CREATE TABLE messages
(
    id              UUID        NOT NULL PRIMARY KEY,
    conversation_id UUID        NOT NULL REFERENCES conversations (id),
    sender_id       UUID        NOT NULL,
    content         TEXT        NOT NULL,
    sent_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_messages_conversation_id ON messages (conversation_id, sent_at DESC);
