# --- !Ups

ALTER TABLE sales_order ADD COLUMN device_type varchar(255) DEFAULT 'WEB';


# --- !Downs

ALTER TABLE member DROP COLUMN if exists device_type;