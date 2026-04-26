CREATE TABLE photos_meta
(
    id          UUID         NOT NULL PRIMARY KEY,
    listing_id  UUID         NOT NULL REFERENCES listings (id) ON DELETE CASCADE,
    s3_key      VARCHAR(512) NOT NULL,
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    uploaded_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_photos_listing_id ON photos_meta (listing_id);
