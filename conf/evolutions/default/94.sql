# --- !Ups

create table product_add_on_merchant (
    id                         bigint,
    product_assign_id          bigint,
    product_id                 bigint,
    merchant_id                bigint,
    product_type               varchar(100),
    is_active                  boolean,
    is_deleted                 boolean,
    created_at                 timestamp not null,
    updated_at                 timestamp not null,
    constraint pk_product_add_on_merchant primary key (id)
);

create sequence product_add_on_merchant_seq;
alter table product_add_on_merchant drop constraint if exists fk_product_add_on_merchant_id_26;
alter table product_add_on_merchant add constraint fk_product_add_on_merchant_id_26 foreign key (merchant_id) references merchant (id);
create index idx_product_add_on_merchant_id_26 on product_add_on_merchant (merchant_id);

alter table product_add_on_merchant drop constraint if exists fk_product_add_on_merchant_product_id_27;
alter table product_add_on_merchant add constraint fk_product_add_on_merchant_product_id_27 foreign key (product_id) references product_merchant (id);
create index idx_product_add_on_merchant_product_id_27 on product_add_on_merchant (product_id);

# --- !Downs

drop table if exists product_add_on_merchant cascade;
drop sequence if exists product_add_on_merchant_seq;