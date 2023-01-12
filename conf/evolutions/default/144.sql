# --- !Ups

alter table orders add column destination_address TEXT default null;


# --- !Downs

alter table orders drop column if exists destination_address;
