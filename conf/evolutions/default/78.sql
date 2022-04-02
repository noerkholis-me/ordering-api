# --- !Ups
create table product_merchant_detail (
      id                            bigint,
      product_type                  varchar(255),
      is_customizable               boolean,
      product_price                 numeric,
      discount_type                 varchar(255),
      discount                      decimal,
      product_price_after_discount  numeric,
      product_image_main            text,
      product_image_1               text,
      product_image_2               text,
      product_image_3               text,
      product_image_4               text,
      product_merchant_id           bigint,
      is_deleted                    boolean,
      created_at                    timestamp not null,
      updated_at                    timestamp not null,
      constraint pk_product_merchant_detail primary key (id)
);

create sequence product_merchant_detail_seq;

alter table product_merchant_detail drop constraint if exists fk_product_merchant_detail_145;
alter table product_merchant_detail add constraint fk_product_merchant_detail_145 foreign key (product_merchant_id) references product_merchant (id);
create index ix_product_merchant_detail_145 on product_merchant_detail (product_merchant_id);

# --- !Downs

drop table if exists product_merchant_detail cascade;

drop sequence if exists product_merchant_detail_seq;