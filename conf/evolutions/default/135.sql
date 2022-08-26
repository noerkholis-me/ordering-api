# --- !Ups
ALTER TABLE product_merchant ADD column no_sku varchar(50);


# --- !Downs
ALTER TABLE product_merchant DROP column if exists no_sku;