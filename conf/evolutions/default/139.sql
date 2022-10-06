# --- !Ups
ALTER TABLE shipper_order_status ADD column notes varchar(200) default null;


# --- !Downs
ALTER TABLE shipper_order_status DROP column if exists notes;