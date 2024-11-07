# --- !Ups

alter table member add column device_token varchar(255) default null;


# --- !Downs

alter table member drop column if exists device_token;