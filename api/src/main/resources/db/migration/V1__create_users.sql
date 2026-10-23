-- V1: Core identity tables for Chanko

-- One row per person, regardless of how they log in
CREATE TABLE users (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email             TEXT        NOT NULL,
    email_verified_at TIMESTAMPTZ,                       -- NULL = not verified
    username          TEXT,                              -- NULL until onboarding
    display_name      TEXT,
    avatar_url        TEXT,
    role              TEXT        NOT NULL DEFAULT 'USER',
    status            TEXT        NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_login_at     TIMESTAMPTZ,

    CONSTRAINT users_role_chk
        CHECK (role IN ('USER', 'MODERATOR', 'ADMIN')),
    CONSTRAINT users_status_chk
        CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
    CONSTRAINT users_username_format_chk
        CHECK (username IS NULL OR username ~ '^[A-Za-z0-9_]{3,20}$')
);

-- Case-insensitive uniqueness
CREATE UNIQUE INDEX users_email_lower_uq    ON users (lower(email));
CREATE UNIQUE INDEX users_username_lower_uq ON users (lower(username));

-- Exists only if the user set a password
CREATE TABLE password_credentials (
    user_id       UUID        PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    password_hash TEXT        NOT NULL,                  -- BCrypt/Argon2, never plaintext
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- One row per linked external login (Google now, others later)
CREATE TABLE auth_identities (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    provider         TEXT        NOT NULL,
    provider_subject TEXT        NOT NULL,               -- Google's stable "sub" claim
    provider_email   TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_used_at     TIMESTAMPTZ,

    CONSTRAINT auth_identities_provider_chk
        CHECK (provider IN ('GOOGLE')),
    CONSTRAINT auth_identities_provider_subject_uq
        UNIQUE (provider, provider_subject),
    CONSTRAINT auth_identities_user_provider_uq
        UNIQUE (user_id, provider)
);

-- Single-use email verification and password reset tokens
CREATE TABLE user_tokens (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    purpose     TEXT        NOT NULL,
    token_hash  TEXT        NOT NULL UNIQUE,             -- SHA-256 of the token
    expires_at  TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT user_tokens_purpose_chk
        CHECK (purpose IN ('EMAIL_VERIFICATION', 'PASSWORD_RESET'))
);

CREATE INDEX user_tokens_user_purpose_idx ON user_tokens (user_id, purpose);
