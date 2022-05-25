# --- !Ups

alter table orders add column store_id bigint;

alter table orders drop constraint if exists fk_store_id_orders;
alter table orders add constraint fk_store_id_orders foreign key (store_id) references store (id);
create index idx_store_id_orders on orders (store_id);

# --- !Downs

drop column if exist store_id;