# --- !Ups
ALTER TABLE merchant ADD column merchant_qr_code varchar(255) default null;


# --- !Downs
ALTER TABLE merchant DROP column if exists merchant_qr_code;