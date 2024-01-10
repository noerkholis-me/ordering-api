# --- !Ups

create table delivery_settings(
    id                        bigint not null,
    is_deleted                boolean,
    store_id                  bigint,
    merchant_id               bigint,
    max_range_delivery        int,
    km_price_value            int,
    flat_price_value          int,
    deliver_fee               int,
    enable_flat_price         boolean,
    max_range_flat_price      int,
    calculate_method          varchar(255),
    created_at                timestamp not null,
    updated_at                timestamp not null,
    constraint pk_delivery_settings primary key (id)
);

create sequence delivery_settings_seq;

alter table delivery_settings add constraint fk_delivery_settings_store foreign key (store_id) references store (id);
alter table delivery_settings add constraint fk_delivery_settings_merchant foreign key (merchant_id) references merchant (id);

create index ix_delivery_settings_store on delivery_settings (store_id);
create index ix_delivery_settings_merchant on delivery_settings (merchant_id);


# --- !Downs
drop table if exists delivery_settings cascade;
drop sequence if exists delivery_settings_seq;