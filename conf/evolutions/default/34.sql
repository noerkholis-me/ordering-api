# --- !Ups


ALTER TABLE product ADD COLUMN customizable boolean;

# --- !Downs

ALTER TABLE product DROP COLUMN if exists customizable;
