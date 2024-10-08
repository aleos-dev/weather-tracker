CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role_id  BIGINT,
    CONSTRAINT users_username_unique UNIQUE (username),
    CONSTRAINT users_role_id_fk FOREIGN KEY (role_id) REFERENCES authorization_role (id) ON DELETE SET NULL

);

INSERT INTO users (username, password, role_id)
VALUES ('admin', 'admin', (SELECT id FROM authorization_role WHERE role = 'ADMIN'));
