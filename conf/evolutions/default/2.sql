# --- !Ups

CREATE TABLE time_entries (
  id SERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  date DATE NOT NULL,
  quantity BIGINT NOT NULL,
  description varchar(255) NOT NULL,
  status varchar(255) NOT NULL
);

CREATE TABLE time_entry_responses (
  id SERIAL PRIMARY KEY,
  manager_id BIGINT NOT NULL,
  entry_id BIGINT NOT NULL,
  date varchar(255) NOT NULL,
  response varchar(255) NOT NULL
);

# --- !Downs

DROP TABLE time_entry_responses;
DROP TABLE time_entries;
