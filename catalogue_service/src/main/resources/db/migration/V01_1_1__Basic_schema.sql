CREATE SCHEMA IF NOT EXISTS catalogue;

CREATE TABLE catalogue.t_product(
  id        serial primary key,
  c_title   varchar(50) NOT NULL CHECK(LENGTH(TRIM(c_title)) >= 3),
  c_details varchar(1000)
);