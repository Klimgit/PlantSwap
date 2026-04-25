CREATE TABLE users
(
    id            UUID         NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    city          VARCHAR(100),
    created_at    TIMESTAMPTZ  NOT NULL             DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL             DEFAULT now()
);
