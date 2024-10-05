CREATE TABLE authorization_role
(
    id   BIGSERIAL PRIMARY KEY,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT authorization_role_role_unique UNIQUE (role)
);

INSERT INTO authorization_role (role)
VALUES ('ADMIN');
INSERT INTO authorization_role (role)
VALUES ('USER');
INSERT INTO authorization_role (role)
VALUES ('ANONYMOUS');
