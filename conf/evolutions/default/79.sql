# --- !Ups
create table product_merchant_description (
         id                            bigint,
         short_description             text,
         long_description              text,
         product_merchant_detail_id    bigint,
         is_deleted                    boolean,
         created_at                    timestamp not null,
         updated_at                    timestamp not null,
         constraint pk_product_merchant_description primary key (id)
);

create sequence product_merchant_description_seq;

alter table product_merchant_description drop constraint if exists fk_product_merchant_description_145;
alter table product_merchant_description add constraint fk_product_merchant_description_145 foreign key (product_merchant_detail_id) references product_merchant_detail (id);
create index ix_product_merchant_description_145 on product_merchant_description (product_merchant_detail_id);

# --- !Downs

drop table if exists product_merchant_description cascade;

drop sequence if exists product_merchant_description_seq;