# --- !Ups

ALTER TABLE cart ADD COLUMN is_deleted boolean;
ALTER TABLE cart_additional_detail ADD COLUMN is_deleted boolean;
ALTER TABLE s_order ADD COLUMN is_deleted boolean;
ALTER TABLE s_order_detail ADD COLUMN is_deleted boolean;
ALTER TABLE s_order_detail_additional ADD COLUMN is_deleted boolean;


# --- !Downs

ALTER TABLE cart DROP COLUMN if exists is_deleted;
ALTER TABLE cart_additional_detail DROP COLUMN if exists is_deleted;
ALTER TABLE s_order DROP COLUMN if exists is_deleted;
ALTER TABLE s_order_detail DROP COLUMN if exists is_deleted;
ALTER TABLE s_order_detail_additional DROP COLUMN if exists is_deleted;
