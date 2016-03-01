# Users schema

# --- !Ups

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  address varchar(255) NOT NULL,
  phone varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  position varchar(255) NOT NULL
);

# --- !Downs

DROP TABLE users;