# --- !Ups


ALTER TABLE s_order_payment add column url_midtrans varchar(255) default null;

# --- !Downs

ALTER TABLE s_order_payment DROP COLUMN if exists url_midtrans;
