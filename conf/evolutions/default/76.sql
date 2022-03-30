# --- !Ups

create table sub_category_merchant (
        id                         bigint,
        subcategory_name           varchar(255),
        image_web                  varchar(255),
        image_mobile               varchar(255),
        merchant_id                bigint,
        category_id                bigint,
        is_active                  boolean,
        is_deleted                 boolean,
        created_at                 timestamp not null,
        updated_at                 timestamp not null,
        constraint pk_sub_category_merchant primary key (id)
    );

create sequence sub_category_merchant_seq;
alter table sub_category_merchant drop constraint if exists fk_sub_category_merchant_145;
alter table sub_category_merchant drop constraint if exists fk_sub_category_merchant_146;
alter table sub_category_merchant add constraint fk_sub_category_merchant_145 foreign key (merchant_id) references merchant (id);
create index ix_sub_category_merchant_145 on sub_category_merchant (merchant_id);
alter table sub_category_merchant add constraint fk_sub_category_merchant_146 foreign key (category_id) references category_merchant (id);
create index ix_sub_category_merchant_146 on sub_category_merchant (category_id);

# --- !Downs

drop table if exists sub_category_merchant cascade;

drop sequence if exists sub_category_merchant_seq;