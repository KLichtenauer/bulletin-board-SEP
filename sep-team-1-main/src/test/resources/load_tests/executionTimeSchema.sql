CREATE SCHEMA IF NOT EXISTS schwarzes_brett_testing;

CREATE TABLE IF NOT EXISTS schwarzes_brett_testing.test
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS schwarzes_brett_testing.execution_time
(
    id        SERIAL PRIMARY KEY,
    test_id   INTEGER REFERENCES schwarzes_brett_testing.test NOT NULL,
    utc_start TIMESTAMP                                       NOT NULL,
    utc_stop  TIMESTAMP                                       NOT NULL
);
