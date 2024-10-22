CREATE TABLE User_verification
(
    id       BIGINT      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    token           UUID      NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    user_id         BIGINT    NOT NULL,

    CONSTRAINT user_verification_user_fk
        FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE
);
