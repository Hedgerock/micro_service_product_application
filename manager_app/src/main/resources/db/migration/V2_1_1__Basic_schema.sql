CREATE SCHEMA IF NOT EXISTS user_management;

CREATE TABLE user_management.t_user(
    id SERIAL PRIMARY KEY,
    c_username VARCHAR NOT NULL CHECK(LENGTH(TRIM(c_username)) > 0) UNIQUE,
    c_password VARCHAR
);

CREATE TABLE user_management.t_authority(
    id SERIAL PRIMARY KEY,
    c_authority VARCHAR NOT NULL CHECK(LENGTH(TRIM(c_authority)) > 0) UNIQUE
);

CREATE TABLE user_management.t_user_authority(
    id SERIAL PRIMARY KEY,
    id_user BIGINT NOT NULL REFERENCES user_management.t_user(id),
    id_authority BIGINT NOT NULL REFERENCES  user_management.t_authority(id),
    CONSTRAINT uk_user_authority UNIQUE (id_user, id_authority)
);