CREATE TABLE Users
(
    id       BIGINT      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR     NOT NULL,
    role_id  BIGINT,
    CONSTRAINT users_username_unique UNIQUE (username),
    CONSTRAINT users_role_id_fk FOREIGN KEY (role_id) REFERENCES Authorization_role (id) ON DELETE SET NULL

);

INSERT INTO Users (username, password, role_id)
VALUES ('admin', 'secret123', (SELECT id FROM Authorization_role WHERE role = 'ADMIN'));
