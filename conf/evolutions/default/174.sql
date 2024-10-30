# --- !Ups

alter table orders add column device_token varchar(255) default null;


# --- !Downs

alter table orders drop column if exists device_token;