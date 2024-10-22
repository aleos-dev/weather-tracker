CREATE TABLE user_location
(

    user_id     bigint NOT NULL,

    location_id bigint NOT NULL,

    name        varchar(20),

    CONSTRAINT user_location_uq UNIQUE (user_id, location_id),
    CONSTRAINT user_location_user_fk FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE,
    CONSTRAINT user_location_location_fk FOREIGN KEY (location_id) REFERENCES Location (id) ON DELETE CASCADE
)