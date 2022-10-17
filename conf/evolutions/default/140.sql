# --- !Ups
ALTER TABLE product_merchant_detail ADD column product_merchant_qr_code varchar(255) default null;
ALTER TABLE product_store ADD column product_store_qr_code varchar(255) default null;


# --- !Downs
ALTER TABLE product_merchant_detail DROP column if exists product_merchant_qr_code;
ALTER TABLE product_store DROP column if exists product_store_qr_code;