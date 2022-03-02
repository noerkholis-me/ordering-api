# --- !Ups


ALTER TABLE s_order ADD COLUMN device varchar(255);
ALTER TABLE s_order ADD COLUMN service_fee float;
ALTER TABLE s_order ADD COLUMN tax float;
ALTER TABLE s_order ADD COLUMN order_type varchar(255);
ALTER TABLE s_order_detail ADD COLUMN note varchar(255);


# --- !Downs

ALTER TABLE s_order DROP COLUMN if exists device;
ALTER TABLE s_order DROP COLUMN if exists service_fee;
ALTER TABLE s_order DROP COLUMN if exists tax;
ALTER TABLE s_order DROP COLUMN if exists order_type;
ALTER TABLE s_order_detail DROP COLUMN if exists note;
