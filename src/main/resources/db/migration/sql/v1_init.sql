DROP SEQUENCE IF EXISTS user_sequence CASCADE;
DROP SEQUENCE IF EXISTS token_sequence CASCADE;

CREATE SEQUENCE user_sequence START 1 INCREMENT 1;
CREATE SEQUENCE token_sequence START 1 INCREMENT 1;

DROP TABLE IF EXISTS Tokens CASCADE;
DROP TABLE IF EXISTS Users CASCADE;

CREATE table tokens
(
    id       SERIAL  not null PRIMARY KEY,
    token    text    not null unique,
    requests integer not null
);

CREATE table users
(
    id       SERIAL not null PRIMARY KEY,
    name     text   not null,
    email    text   not null unique,
    password text   not null,
    token_id integer references tokens (id)
);
