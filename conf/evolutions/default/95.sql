# --- !Ups

create table product_add_on_type (
    id                         bigint,
    product_type               varchar(100),
    merchant_id                bigint,
    is_active                  boolean,
    is_deleted                 boolean,
    created_at                 timestamp not null,
    updated_at                 timestamp not null,
    constraint pk_product_add_on_type primary key (id)
);

create sequence product_add_on_type_seq;
alter table product_add_on_type drop constraint if exists fk_product_add_on_type_merchant_id_26;
alter table product_add_on_type add constraint fk_product_add_on_type_merchant_id_26 foreign key (merchant_id) references merchant (id);
create index idx_product_add_on_type_merchant_id_26 on product_add_on_type (merchant_id);

# --- !Downs

drop table if exists product_add_on_type cascade;
drop sequence if exists product_add_on_type_seq;