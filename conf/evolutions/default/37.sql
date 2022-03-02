# --- !Ups

create table cart (
  id                        bigint not null,
  status                    varchar(255),
  member_id                 bigint,
  product_id                bigint,
  quantity                  integer,
  price                  	float,
  total_price               float,
  discount                 	float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_cart primary key (id))
;

create table cart_additional_detail (
  id                        bigint not null,
  status                    varchar(255),
  product_id                bigint,
  price                  	float,
  discount                 	float,
  cart_id					bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_cart_additional_detail primary key (id))
;

create table s_order (
  id						bigint not null,
  order_number              varchar(255),
  order_date                timestamp not null,
  status                    varchar(255),
  member_id                 bigint,
  discount                 	float,
  total_price				float,
  payment_type              varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_s_order primary key (id))
;

create table s_order_detail (
  id                        bigint not null,
  order_id					bigint,
  product_id                bigint,
  quantity                  integer,
  price                  	float,
  total_price               float,
  discount                 	float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_s_order_detail primary key (id))
;

create table s_order_detail_additional (
  id                        bigint not null,
  detail_id					bigint,
  product_id                bigint,
  price                  	float,
  discount                 	float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_s_order_detail_additional primary key (id))
;

create sequence cart_seq;
create sequence cart_additional_detail_seq;
create sequence s_order_seq;
create sequence s_order_detail_seq;
create sequence s_order_detail_additional_seq;

alter table cart add constraint fk_cart_member foreign key (member_id) references member (id);
create index ix_cart_member on cart (member_id);

alter table cart add constraint fk_cart_product foreign key (product_id) references product (id);
create index ix_cart_product on cart (product_id);

alter table cart_additional_detail add constraint fk_cart_additional_detail_product foreign key (product_id) references product (id);
create index ix_cart_additional_detail_product on cart_additional_detail (product_id);

alter table cart_additional_detail add constraint fk_cart_additional_detail_cart foreign key (cart_id) references cart (id);
create index ix_cart_additional_detail_cart on cart_additional_detail (cart_id);

alter table s_order add constraint fk_s_order_member foreign key (member_id) references member (id);
create index ix_s_order_member on cart (member_id);

alter table s_order_detail add constraint fk_s_order_detail_product foreign key (product_id) references product (id);
create index ix_s_order_detail_product on s_order_detail (product_id);

alter table s_order_detail add constraint fk_s_order_detail_s_order foreign key (order_id) references s_order (id);
create index ix_s_order_detail_order on s_order_detail (order_id);

alter table s_order_detail_additional add constraint fk_s_order_detail_additional_s_order_detail foreign key (detail_id) references s_order_detail (id);
create index ix_s_order_detail_additional_order_detail on s_order_detail_additional (detail_id);

alter table s_order_detail_additional add constraint fk_s_order_detail_additional_product foreign key (product_id) references product (id);
create index ix_s_order_detail_additional_product on s_order_detail_additional (product_id);

# --- !Downs

drop table if exists cart cascade;
drop sequence if exists cart_seq;

drop table if exists cart_additional_detail cascade;
drop sequence if exists cart_additional_detail_seq;

drop table if exists s_order cascade;
drop sequence if exists s_order_seq;

drop table if exists s_order_detail cascade;
drop sequence if exists s_order_detail_seq;

drop table if exists s_order_detail_additional cascade;
drop sequence if exists s_order_detail_additional_seq;
