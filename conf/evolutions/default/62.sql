# --- !Ups


ALTER TABLE s_order add column full_services_fee double precision default 0.0;

# --- !Downs

ALTER TABLE s_order DROP COLUMN if exists full_services_fee;
