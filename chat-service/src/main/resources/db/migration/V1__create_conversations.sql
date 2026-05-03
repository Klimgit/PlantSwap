CREATE TABLE conversations
(
    id           UUID        NOT NULL PRIMARY KEY,
    owner_id     UUID        NOT NULL,
    requester_id UUID        NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
