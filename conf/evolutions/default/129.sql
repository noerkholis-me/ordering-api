# --- !Ups
ALTER TABLE merchant_payment ADD column type_payment varchar(100);


# --- !Downs
ALTER TABLE merchant_payment DROP column if exists type_payment;
