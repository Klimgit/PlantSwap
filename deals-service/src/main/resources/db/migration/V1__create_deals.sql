CREATE TABLE deals
(
    id           UUID        NOT NULL PRIMARY KEY,
    listing_id   UUID        NOT NULL,
    owner_id     UUID        NOT NULL,
    requester_id UUID        NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note         VARCHAR(500),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_not_self_deal CHECK (owner_id <> requester_id)
);

CREATE INDEX idx_deals_owner_id     ON deals (owner_id);
CREATE INDEX idx_deals_requester_id ON deals (requester_id);
CREATE INDEX idx_deals_listing_id   ON deals (listing_id);
CREATE INDEX idx_deals_status       ON deals (status);
