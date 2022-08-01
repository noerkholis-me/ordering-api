# --- !Ups
ALTER TABLE role_merchant ADD COLUMN is_cashier BOOLEAN DEFAULT false;

# --- !Downs

ALTER TABLE DROP COLUMN if exists is_cashier;