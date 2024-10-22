CREATE TABLE Location
(
    id        BIGINT           NOT NULL GENERATED ALWAYS AS IDENTITY,

    longitude DOUBLE PRECISION NOT NULL,

    latitude  DOUBLE PRECISION NOT NULL,

    NAME      VARCHAR(20)      NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT location_long_lat_uq UNIQUE (longitude, latitude)
)