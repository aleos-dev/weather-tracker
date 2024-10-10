ALTER TABLE users
    ADD COLUMN email varchar(100) DEFAULT 'default@example.com' NOT NULL UNIQUE;

UPDATE users
SET email = CONCAT(username, '@gmail.com')
WHERE email = 'default@example.com';

ALTER TABLE users
    ALTER COLUMN email DROP DEFAULT;
