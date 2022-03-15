# --- !Ups


ALTER TABLE s_order add column shipper_id varchar(24) DEFAULT null;

# --- !Downs

ALTER TABLE s_order DROP COLUMN if exists shipper_id;
