# --- !Ups


ALTER TABLE s_order_detail add column store_id bigint DEFAULT null;

# --- !Downs

ALTER TABLE s_order_detail DROP COLUMN if exists store_id;
