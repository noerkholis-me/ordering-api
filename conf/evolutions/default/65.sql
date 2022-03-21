# --- !Ups
ALTER TABLE merchant ADD COLUMN IF NOT EXISTS role_id bigint;

ALTER TABLE merchant ADD CONSTRAINT fk_merchant_role FOREIGN KEY (role_id) REFERENCES role (id);

CREATE INDEX ix_merchant_role ON merchant (role_id);

# --- !Downs

ALTER TABLE merchant DROP COLUMN if exists role_id;