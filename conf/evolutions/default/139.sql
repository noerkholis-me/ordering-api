# --- !Ups
ALTER TABLE merchant ADD column merchant_type varchar(100) default null;


# --- !Downs
ALTER TABLE merchant DROP column if exists merchant_type;