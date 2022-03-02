# --- !Ups

ALTER TABLE sales_order_seller
ADD COLUMN shipment_type 			bigint,
ADD COLUMN pick_up_point_name		varchar(255),
ADD COLUMN pick_up_point_address	TEXT,
ADD COLUMN pick_up_point_contact	varchar(255),
ADD COLUMN pick_up_point_duration	bigint,
ADD COLUMN pick_up_point_latitude	float,
ADD COLUMN pick_up_point_longitude	float;

# --- !Downs

ALTER TABLE sales_order_seller
DROP COLUMN shipment_type,
DROP COLUMN pick_up_point_name,
DROP COLUMN pick_up_point_address,
DROP COLUMN pick_up_point_contact,
DROP COLUMN pick_up_point_duration,
DROP COLUMN pick_up_point_latitude,
DROP COLUMN pick_up_point_longitude;