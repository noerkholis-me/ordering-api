# --- !Ups
alter table user_merchant drop column if exists merchant_id;

# --- !Downs
alter table user_merchant add column merchant_id bigint;