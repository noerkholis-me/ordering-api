# --- !Ups
ALTER TABLE orders ADD column device_type varchar(20);


# --- !Downs
ALTER TABLE orders DROP column if exists device_type;