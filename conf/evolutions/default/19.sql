# --- !Ups
create table master_diamond_inventory (
  id                        bigint not null,
  is_deleted                boolean,
  user_id                   bigint not null,
  size_in_carat             float,
  quantity_in_stock         bigint,
  master_diamond_price_id   bigint not null,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_master_diamond_inventory primary key (id))
;

ALTER TABLE master_diamond_inventory ADD CONSTRAINT master_diamond_inventory_fk_user_cms FOREIGN KEY (user_id) REFERENCES public."user_cms"(id);
ALTER TABLE master_diamond_inventory ADD CONSTRAINT master_diamond_inventory_fk_master_diamond_price FOREIGN KEY (master_diamond_price_id) REFERENCES public."master_diamond_price"(id);

create sequence master_diamond_inventory_seq;

# --- !Downs
drop table if exists master_diamond_inventory;
drop sequence if exists master_diamond_inventory_seq;