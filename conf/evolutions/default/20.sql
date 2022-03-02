# --- !Ups

create table diamond_type (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_diamond_type primary key (id))
;

ALTER TABLE master_diamond_price ADD COLUMN diamond_type_id bigint DEFAULT null;

ALTER TABLE master_diamond_inventory ADD COLUMN diamond_type_id bigint DEFAULT null;

ALTER TABLE master_diamond_inventory ADD COLUMN clarity varchar(255);

ALTER TABLE master_diamond_inventory ADD COLUMN color varchar(255);

alter table master_diamond_price add constraint master_diamond_price_fk_dimond_type foreign key (diamond_type_id) references public."diamond_type"(id);

alter table master_diamond_inventory add constraint master_diamond_inventory_fk_dimond_type foreign key (diamond_type_id) references public."diamond_type"(id);

create sequence diamond_type_seq;
# --- !Downs

alter table master_diamond_price drop constraint if exists master_diamond_price_fk_dimond_type;

ALTER TABLE master_diamond_price DROP COLUMN if exists diamond_type_id;

alter table master_diamond_inventory drop constraint if exists master_diamond_inventory_fk_dimond_type;

ALTER TABLE master_diamond_inventory DROP COLUMN if exists diamond_type_id;

ALTER TABLE master_diamond_inventory DROP COLUMN if exists clarity;

ALTER TABLE master_diamond_inventory DROP COLUMN if exists color;

drop table if exists diamond_type;

drop sequence if exists diamond_type_seq;