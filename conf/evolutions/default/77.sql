# --- !Ups
create table product_merchant (
       id                         bigint,
       product_name               varchar(255),
       category_merchant_id       bigint,
       sub_category_merchant_id   bigint,
       brand_merchant_id          bigint,
       merchant_id                bigint,
       is_active                  boolean,
       is_deleted                 boolean,
       created_at                 timestamp not null,
       updated_at                 timestamp not null,
       constraint pk_product_merchant primary key (id)
);

create sequence product_merchant_seq;

alter table product_merchant drop constraint if exists fk_product_merchant_145;
alter table product_merchant add constraint fk_product_merchant_145 foreign key (merchant_id) references merchant (id);
create index ix_product_merchant_145 on product_merchant (merchant_id);

alter table product_merchant drop constraint if exists fk_product_category_146;
alter table product_merchant add constraint fk_product_category_146 foreign key (category_merchant_id) references category_merchant (id);
create index ix_product_category_146 on product_merchant (category_merchant_id);

alter table product_merchant drop constraint if exists fk_product_sub_category_147;
alter table product_merchant add constraint fk_product_sub_category_147 foreign key (sub_category_merchant_id) references sub_category_merchant (id);
create index ix_product_sub_category_147 on product_merchant (sub_category_merchant_id);

alter table product_merchant drop constraint if exists fk_product_brand_merchant_148;
alter table product_merchant add constraint fk_product_brand_merchant_148 foreign key (brand_merchant_id) references brand_merchant (id);
create index ix_product_brand_merchant_148 on product_merchant (brand_merchant_id);

# --- !Downs

drop table if exists product_merchant cascade;

drop sequence if exists product_merchant_seq;