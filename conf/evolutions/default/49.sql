# --- !Ups


ALTER TABLE s_order_status add column notes TEXT;
ALTER TABLE s_order_status add column order_id bigint;

# --- !Downs

ALTER TABLE s_order_status DROP COLUMN if exists notes;
ALTER TABLE s_order_status DROP COLUMN if exists order_id;