# --- !Ups

alter table order_detail add column product_id bigint;

alter table order_detail drop constraint if exists fk_order_detail_product_id;
alter table order_detail add constraint fk_order_detail_product_id foreign key (product_id) references product_merchant (id);
create index idx_product_merchant on order_detail (product_id);

# --- !Downs

drop column if exists product_id cascade;
drop column if exists product_store_id cascade;