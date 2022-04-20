# --- !Ups
alter table product_merchant add column subs_category_merchant_id bigint;

alter table product_merchant drop constraint if exists fk_product_merchant_149;
alter table product_merchant add constraint fk_product_merchant_149 foreign key (subs_category_merchant_id) references subs_category_merchant (id);
create index ix_product_merchant_149 on product_merchant (subs_category_merchant_id);

# --- !Downs
alter table product_merchant drop column if exists subs_category_merchant_id;
