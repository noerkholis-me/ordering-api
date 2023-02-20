# --- !Ups

alter table orders add column reference_number varchar(255) default null;


# --- !Downs

alter table orders drop column if exists reference_number;
