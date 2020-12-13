CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled  BOOLEAN DEFAULT(TRUE),
    locked   BOOLEAN DEFAULT(FALSE)
);

CREATE TABLE IF NOT EXISTS user_authorities (
    id        SERIAL PRIMARY KEY,
    username  VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL
);

