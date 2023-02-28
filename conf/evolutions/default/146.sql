# --- !Ups

alter table merchant add column product_store_required boolean default false;
update merchant set product_store_required = false;


# --- !Downs

alter table merchant drop column if exists product_store_required;
