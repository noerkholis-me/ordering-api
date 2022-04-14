# --- !Ups

create table orders (
    id                         bigint,
    order_date                 timestamp,
    order_number               varchar(255),
    order_type                 varchar(20),
    sub_total                  numeric,
    total_price                numeric,
    status                     varchar(20),
    user_id                    bigint,
    approved_by                varchar(30),
    approved_date              timestamp,
    is_deleted                 boolean,
    created_at                 timestamp not null,
    updated_at                 timestamp not null,
    constraint uq_order_order_number unique (order_number),
    constraint pk_order primary key (id)
);

create sequence order_seq;
alter table orders drop constraint if exists fk_order_user_id;
alter table orders add constraint fk_order_user_id foreign key (user_id) references member (id);
create index idx_order_user_id on orders (user_id);

create table order_detail (
      id                         bigint,
      order_id                   bigint,
      product_store_id           bigint,
      product_name               varchar(255),
      product_price              numeric,
      quantity                   int,
      notes                      text,
      is_deleted                 boolean,
      created_at                 timestamp not null,
      updated_at                 timestamp not null,
      constraint pk_order_detail primary key (id)
);

create sequence order_detail_seq;
alter table order_detail drop constraint if exists fk_order_detail_order_id;
alter table order_detail add constraint fk_order_detail_order_id foreign key (order_id) references orders (id);
alter table order_detail drop constraint if exists fk_order_detail_product_store_id;
alter table order_detail add constraint fk_order_detail_product_store_id foreign key (product_store_id) references product_store (id);
create index idx_order_detail_order_id on order_detail (order_id);
create index idx_order_detail_product_store_id on order_detail (product_store_id);

create table order_payment (
       id                         bigint,
       order_id                   bigint,
       invoice_no                 varchar(255),
       status                     varchar(20),
       payment_type               varchar(20),
       payment_channel            varchar(20),
       total_amount               numeric,
       payment_date               timestamp,
       is_deleted                 boolean,
       created_at                 timestamp not null,
       updated_at                 timestamp not null,
       constraint uq_order_payment_invoice_no unique (invoice_no),
       constraint pk_order_payment primary key (id)
);

create sequence order_payment_seq;
alter table order_payment drop constraint if exists fk_order_payment_order_id;
alter table order_payment add constraint fk_order_payment_order_id foreign key (order_id) references orders (id);
create index idx_order_payment_order_id on order_payment (order_id);

create table payment_detail (
        id                         bigint,
        order_number               varchar (255),
        reference_id               varchar (255),
        status                     varchar (20),
        total_amount               numeric,
        payment_channel            varchar (20),
        creation_time              timestamp,
        qr_code                    text,
        order_payment_id           bigint,
        is_deleted                 boolean,
        created_at                 timestamp not null,
        updated_at                 timestamp not null,
        constraint pk_payment_detail primary key (id)
);

create sequence payment_detail_seq;
alter table payment_detail drop constraint if exists fk_payment_detail_order_payment_id;
alter table payment_detail add constraint fk_payment_detail_order_payment_id foreign key (order_payment_id) references order_payment (id);
create index idx_payment_detail_order_payment_id on payment_detail (order_payment_id);

# --- !Downs

drop table if exists order cascade;
drop sequence if exists order_seq;
drop table if exists order_detail cascade;
drop sequence if exists order_detail_seq;
drop table if exists order_payment cascade;
drop sequence if exists order_payment_seq;
drop table if exists payment_detail cascade;
drop sequence if exists payment_detail_seq;
