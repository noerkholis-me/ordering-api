# --- !Ups

alter table store add column store_alias varchar(50) default null;

# --- !Downs

alter table store drop column if exists store_alias;