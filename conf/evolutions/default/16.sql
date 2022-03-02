# --- !Ups

ALTER TABLE voucher ADD COLUMN device_source int default 6;

# --- !Downs

ALTER TABLE voucher DROP COLUMN if exists device_source;