# --- !Ups

alter table role_merchant add column is_waiters BOOLEAN default false;


# --- !Downs

alter table role_merchant drop column if exists is_waiters;