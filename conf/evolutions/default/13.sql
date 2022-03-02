# --- !Ups

ALTER TABLE product ADD COLUMN checkout_type bigint;
ALTER TABLE sales_order ADD COLUMN checkout_type bigint;

# --- !Downs

ALTER TABLE sales_order DROP COLUMN if exists checkout_type;
ALTER TABLE product DROP COLUMN if exists checkout_type;