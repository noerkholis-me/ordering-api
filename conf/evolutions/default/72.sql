# --- !Ups

create table brand_merchant (
        id                         bigint,
        brand_name                 varchar(255),
        image_web                  varchar(255),
        image_mobile               varchar(255),
        merchant_id                bigint,
        is_active                  boolean,
        is_deleted                 boolean,
        created_at                 timestamp not null,
        updated_at                 timestamp not null,
        constraint pk_brand_merchant primary key (id)
    );

create sequence brand_merchant_seq;
alter table brand_merchant drop constraint if exists fk_brand_merchant_141;
alter table brand_merchant add constraint fk_brand_merchant_141 foreign key (merchant_id) references merchant (id);
create index ix_brand_merchant_141 on brand_merchant (merchant_id);

# --- !Downs

drop table if exists brand_merchant cascade;

drop sequence if exists brand_merchant_seq;