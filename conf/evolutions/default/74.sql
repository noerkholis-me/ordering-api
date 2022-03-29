# --- !Ups

create table category_merchant (
        id                         bigint,
        category_name                 varchar(255),
        image_web                  varchar(255),
        image_mobile               varchar(255),
        merchant_id                bigint,
        is_active                  boolean,
        is_deleted                 boolean,
        created_at                 timestamp not null,
        updated_at                 timestamp not null,
        constraint pk_category_merchant primary key (id)
    );

create sequence category_merchant_seq;
alter table category_merchant drop constraint if exists fk_category_merchant_145;
alter table category_merchant add constraint fk_category_merchant_145 foreign key (merchant_id) references merchant (id);
create index ix_category_merchant_145 on category_merchant (merchant_id);

# --- !Downs

drop table if exists category_merchant cascade;

drop sequence if exists category_merchant_seq;