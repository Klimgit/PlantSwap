CREATE TABLE processed_events
(
    event_id     UUID        NOT NULL PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
