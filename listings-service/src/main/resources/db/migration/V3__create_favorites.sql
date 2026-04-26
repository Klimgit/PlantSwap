CREATE TABLE favorites
(
    user_id    UUID        NOT NULL,
    listing_id UUID        NOT NULL REFERENCES listings (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, listing_id)
);

CREATE INDEX idx_favorites_user_id ON favorites (user_id);
