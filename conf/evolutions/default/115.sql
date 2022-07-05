# --- !Ups
alter table user_merchant drop constraint if exists uq_user_merchant_email;

# --- !Downs