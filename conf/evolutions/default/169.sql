# --- !Ups

alter table product_merchant add column product_merchant_detail_id               bigint default null;


# --- !Downs

alter table product_merchant drop column if exist product_merchant_detail_id;