# --- !Ups

alter table role_merchant add column is_kitchen BOOLEAN default false;


# --- !Downs

alter table role_merchant drop column if exists is_kitchen;