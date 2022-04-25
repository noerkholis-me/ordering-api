# --- !Ups

create table table_type (
    id                         bigint,
    name                       varchar(20),
    minimum_table_count        integer default 1,
    maximum_table_count        integer default 1,
    merchant_id                bigint,
    is_deleted                 boolean,
    created_at                 timestamp not null,
    updated_at                 timestamp not null,
    constraint pk_table_type primary key (id)
);

create sequence table_type_seq;
alter table table_type drop constraint if exists fk_table_type_merchant_id;
alter table table_type add constraint fk_table_type_merchant_id foreign key (merchant_id) references merchant (id);
create index idx_table_type_merchant_id on table_type (merchant_id);

create table table_merchant (
    id                         bigint,
    name                       varchar(50),
    store_id                   bigint,
    table_type_id              bigint,
    is_active                  boolean,
    is_deleted                 boolean,
    created_at                 timestamp not null,
    updated_at                 timestamp not null,
    constraint pk_table_merchant primary key (id)
);

create sequence table_merchant_seq;
alter table table_merchant drop constraint if exists fk_table_merchant_store_id;
alter table table_merchant add constraint fk_table_merchant_store_id foreign key (store_id) references store (id);
create index idx_table_store_146 on table_merchant (store_id);

alter table table_merchant drop constraint if exists fk_table_merchant_table_type_id;
alter table table_merchant add constraint fk_table_merchant_table_type_id foreign key (table_type_id) references table_type (id);
create index idx_tables_table_type_146 on table_merchant (table_type_id);


# --- !Downs

drop table if exists table_type cascade;
drop sequence if exists table_type_seq;
drop table if exists table_merchant cascade;
drop sequence if exists table_merchant_seq;