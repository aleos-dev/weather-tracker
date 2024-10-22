CREATE TABLE Authorization_role
(
    id   BIGINT      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    role VARCHAR(50) NOT NULL,

    CONSTRAINT authorization_role_role_unique UNIQUE (role)
);

INSERT INTO Authorization_role (role)
VALUES ('ADMIN'),
       ('USER'),
       ('ANONYMOUS');
