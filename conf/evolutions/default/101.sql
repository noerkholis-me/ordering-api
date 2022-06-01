# --- !Ups

create table order_detail_add_on (
      id                                bigint,
      order_detail_id                   bigint,
      product_add_on_id                 bigint,
      product_assign_id                 bigint,
      product_name                      varchar(255),
      product_price                     numeric,
      quantity                          int,
      notes                             text,
      is_deleted                        boolean,
      created_at                        timestamp not null,
      updated_at                        timestamp not null,
      constraint pk_order_detail_add_on primary key (id)
);

create sequence order_detail_add_on_seq;
alter table order_detail_add_on drop constraint if exists fk_order_detail_id;
alter table order_detail_add_on add constraint fk_order_detail_id foreign key (order_detail_id) references order_detail (id);
alter table order_detail_add_on drop constraint if exists fk_order_detail_product_add_on_id;
alter table order_detail_add_on add constraint fk_order_detail_product_add_on_id foreign key (product_add_on_id) references product_add_on_merchant (id);
create index idx_order_detail_add_on_id on order_detail_add_on (order_detail_id);
create index idx_order_detail_product_add_on_id on order_detail_add_on (product_add_on_id);


alter table orders add column pickup_point_id bigint;
alter table orders add column pickup_point_name varchar(100);
alter table orders add column table_id bigint;
alter table orders add column table_name varchar(100);

alter table orders drop constraint if exists fk_order_pickup_point_id;
alter table orders add constraint fk_order_pickup_point_id foreign key (pickup_point_id) references pick_up_point_merchant (id);
create index idx_pickup_point on orders (pickup_point_id);

alter table orders drop constraint if exists fk_order_table_id;
alter table orders add constraint fk_order_table_id foreign key (table_id) references table_merchant (id);
create index idx_table on orders (table_id);

# --- !Downs

drop table if exists order_detail_add_on cascade;
drop sequence if exists order_detail_add_on_seq;
drop column if exists pickup_point_id cascade;
drop column if exists table_id cascade;