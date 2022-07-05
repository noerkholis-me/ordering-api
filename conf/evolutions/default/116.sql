# --- !Ups
alter table merchant drop constraint if exists uq_merchant_email;

# --- !Downs