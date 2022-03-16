# --- !Ups


ALTER TABLE member add column is_general_cust boolean default false;

# --- !Downs

ALTER TABLE member DROP COLUMN if exists is_general_cust;
