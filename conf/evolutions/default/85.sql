# --- !Ups

create table product_store (
        id                         bigint,
        product_id                 bigint,
        store_id                   bigint,
        store_price                numeric,
        discount_type              varchar(100),
        discount                   decimal,
        final_price                numeric,
        is_active                  boolean,
        is_deleted                 boolean,
        merchant_id                bigint,
        created_at                 timestamp not null,
        updated_at                 timestamp not null,
        constraint pk_product_store primary key (id)
    );

create sequence product_store_seq;
alter table product_store drop constraint if exists fk_product_store_145;
alter table product_store drop constraint if exists fk_product_store_146;
alter table product_store drop constraint if exists fk_product_store_147;
alter table product_store add constraint fk_product_store_145 foreign key (merchant_id) references merchant (id);
create index ix_product_store_145 on product_store (merchant_id);
alter table product_store add constraint fk_product_store_146 foreign key (store_id) references store (id);
create index ix_product_store_146 on product_store (store_id);
alter table product_store add constraint fk_product_store_147 foreign key (product_id) references product_merchant (id);
create index ix_product_store_147 on product_store (product_id);

# --- !Downs

drop table if exists product_store cascade;

drop sequence if exists product_store_seq;