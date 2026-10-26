-- V1: users table for Chanko accounts

CREATE TABLE users (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    username          TEXT        NOT NULL,
    email             TEXT        NOT NULL,
    password_hash     TEXT,
    role              TEXT        NOT NULL DEFAULT 'USER',
    email_verified_at TIMESTAMPTZ,
    last_login_at     TIMESTAMPTZ,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT users_role_check
        CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT users_username_format_check
        CHECK (username ~ '^[A-Za-z0-9_]{3,20}$')
);

CREATE UNIQUE INDEX users_username_lower_uq ON users (lower(username));
CREATE UNIQUE INDEX users_email_lower_uq    ON users (lower(email));