# --- !Ups

create table stock_history(
    id                        bigint not null,
    is_deleted                boolean,
    product_store_id          bigint,
    merchant_id          bigint,
    stock                     int,
    stock_changes             int,
    notes                     varchar(255),
    created_at                timestamp not null,
    updated_at                timestamp not null,
    constraint pk_stock_history primary key (id)
);

create sequence stock_history_seq;

alter table stock_history add constraint fk_stock_history_product_store foreign key (product_store_id) references product_store (id);
alter table stock_history add constraint fk_stock_history_merchant foreign key (merchant_id) references merchant (id);

create index ix_stock_history_product_store on stock_history (product_store_id);


# --- !Downs
drop table if exists stock_history cascade;
drop sequence if exists stock_history_seq;