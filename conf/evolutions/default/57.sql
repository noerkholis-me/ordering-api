# --- !Ups


ALTER TABLE s_order add column active_shipper varchar(7) DEFAULT null;

# --- !Downs

ALTER TABLE s_order DROP COLUMN if exists active_shipper;
