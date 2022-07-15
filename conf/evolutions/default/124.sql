# --- !Ups
alter table orders add column user_merchant_id bigint;
alter table orders drop constraint if exists fk_orders_user_merchant_id;
alter table orders add constraint fk_orders_user_merchant_id foreign key (user_merchant_id) references user_merchant (id);
create index idx_orders_user_merchant_id on session_cashier (user_merchant_id);


# --- !Downs
alter table orders drop column if exists user_merchant_id;