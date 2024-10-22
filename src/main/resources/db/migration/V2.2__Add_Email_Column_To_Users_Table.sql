ALTER TABLE Users
    ADD COLUMN email varchar(100) DEFAULT 'default@example.com' NOT NULL UNIQUE;

UPDATE Users
SET email = CONCAT(username, '@gmail.com')
WHERE email = 'default@example.com';

ALTER TABLE Users
    ALTER COLUMN email DROP DEFAULT;
