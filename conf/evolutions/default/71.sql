# --- !Ups

ALTER TABLE merchant drop column if exists role_id;
ALTER TABLE merchant drop constraint if exists fk_merchant_role;

ALTER TABLE merchant ADD COLUMN role_merchant_id bigint;
ALTER TABLE merchant ADD CONSTRAINT fk_merchant_role FOREIGN KEY (role_merchant_id) REFERENCES role_merchant (id);

CREATE INDEX ix_merchant_role_merchant ON merchant (role_merchant_id);

# --- !Downs

ALTER TABLE merchant DROP COLUMN if exists role_merchant_id;