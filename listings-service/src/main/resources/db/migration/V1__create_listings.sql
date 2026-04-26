CREATE TABLE listings
(
    id             UUID         NOT NULL PRIMARY KEY,
    owner_id       UUID         NOT NULL,
    type           VARCHAR(20)  NOT NULL,
    title          VARCHAR(120) NOT NULL,
    description    TEXT,
    price_amount   NUMERIC(10, 2),
    price_currency VARCHAR(3),
    city           VARCHAR(100),
    status         VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_listings_owner_id ON listings (owner_id);
CREATE INDEX idx_listings_status   ON listings (status);
CREATE INDEX idx_listings_type     ON listings (type);
CREATE INDEX idx_listings_city     ON listings (city);
