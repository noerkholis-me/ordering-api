# --- !Ups

create table subs_category_merchant (
        id                         bigint,
        subscategory_name           varchar(255),
        image_web                  varchar(255),
        image_mobile               varchar(255),
        merchant_id                bigint,
        category_id                bigint,
        subcategory_id             bigint,
        is_active                  boolean,
        is_deleted                 boolean,
        created_at                 timestamp not null,
        updated_at                 timestamp not null,
        sequence                   int,
        constraint pk_subs_category_merchant primary key (id)
    );

create sequence subs_category_merchant_seq;
alter table subs_category_merchant drop constraint if exists fk_subs_category_merchant_145;
alter table subs_category_merchant drop constraint if exists fk_subs_category_merchant_146;
alter table subs_category_merchant drop constraint if exists fk_subs_category_merchant_147;
alter table subs_category_merchant add constraint fk_subs_category_merchant_145 foreign key (merchant_id) references merchant (id);
create index ix_subs_category_merchant_145 on subs_category_merchant (merchant_id);
alter table subs_category_merchant add constraint fk_subs_category_merchant_146 foreign key (category_id) references category_merchant (id);
create index ix_subs_category_merchant_146 on subs_category_merchant (category_id);
alter table subs_category_merchant add constraint fk_subs_category_merchant_147 foreign key (subcategory_id) references sub_category_merchant (id);
create index ix_subs_category_merchant_147 on subs_category_merchant (subcategory_id);

# --- !Downs

drop table if exists subs_category_merchant cascade;

drop sequence if exists subs_category_merchant_seq;