CREATE TABLE IF NOT EXISTS user_files(
     id        SERIAL PRIMARY KEY,
     username  VARCHAR(255) NOT NULL,
     filename  VARCHAR(255) NOT NULL
);