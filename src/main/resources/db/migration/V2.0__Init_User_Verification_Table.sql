CREATE TABLE user_verification
(
    id              BIGSERIAL PRIMARY KEY,
    token           UUID      NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    user_id         BIGINT    NOT NULL,
    CONSTRAINT user_verification_user_fk
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
