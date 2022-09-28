# --- !Ups
ALTER TABLE orders ADD column shipper_order_id varchar(100) default null;


# --- !Downs
ALTER TABLE orders DROP column if exists shipper_order_id;