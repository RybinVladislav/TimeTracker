# --- !Ups

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username varchar(255) NOT NULL,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  address varchar(255) NOT NULL,
  phone varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  position varchar(255) NOT NULL,
  user_role varchar(255) NOT NULL
);

CREATE TABLE login_infos (
  id SERIAL PRIMARY KEY,
  provider_id varchar(255) NOT NULL,
  provider_key varchar(255) NOT NULL
);

CREATE TABLE user_login_infos (
  user_id BIGINT NOT NULL,
  login_info_id BIGINT NOT NULL
);

CREATE TABLE password_infos (
  hasher varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  salt varchar(255),
  login_info_id BIGINT NOT NULL
);

# --- !Downs
DROP TABLE password_infos;
DROP TABLE user_login_infos;
DROP TABLE login_infos;
DROP TABLE users;