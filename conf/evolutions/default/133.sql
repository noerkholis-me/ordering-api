# --- !Ups
ALTER TABLE order_payment ADD column mail_status_code varchar(20);
ALTER TABLE order_payment ADD column mail_status varchar(20);
ALTER TABLE order_payment ADD column mail_message text;


# --- !Downs
ALTER TABLE order_payment DROP column if exists mail_status_code;
ALTER TABLE order_payment DROP column if exists mail_status;
ALTER TABLE order_payment DROP column if exists mail_message;