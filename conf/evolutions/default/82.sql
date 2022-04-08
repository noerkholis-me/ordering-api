# --- !Ups
create table banners (
         id                            bigint,
         banner_name                   varchar(100) not null,
         banner_image_web              text,
         banner_image_mobile           text,
         is_deleted                    boolean,
         is_active                     boolean,
         date_from                     timestamp not null,
         date_to                       timestamp not null,
         merchant_id                   bigint,
         created_at                    timestamp not null,
         updated_at                    timestamp not null,
         constraint pk_banners_id primary key (id)
);

create sequence banners_seq;

alter table banners drop constraint if exists fk_merchant_145;
alter table banners add constraint fk_merchant_145 foreign key (merchant_id) references merchant (id);
create index ix_merchant_145 on banners (merchant_id);

# --- !Downs

drop table if exists banners cascade;

drop sequence if exists banners_seq;