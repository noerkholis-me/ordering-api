# --- !Ups

create table store_access (
      id                                bigint,
      user_merchant_id                  bigint,
      merchant_id                       bigint,
      store_id                          bigint,
      is_active                         boolean,
      is_deleted                        boolean,
      created_at                        timestamp not null,
      updated_at                        timestamp not null,
      constraint pk_store_access primary key (id)
);

create sequence store_access_seq;
alter table store_access drop constraint if exists fk_user_merchant_id;
alter table store_access add constraint fk_user_merchant_id foreign key (user_merchant_id) references user_merchant (id);
alter table store_access drop constraint if exists fk_merchant_id;
alter table store_access add constraint fk_merchant_id foreign key (merchant_id) references merchant (id);
alter table store_access drop constraint if exists fk_store_id;
alter table store_access add constraint fk_store_id foreign key (store_id) references store (id);
create index idx_store_access_user_merchant_id on store_access (user_merchant_id);
create index idx_merchant_id on store_access (merchant_id);
create index idx_store_id on store_access (store_id);


# --- !Downs

drop table if exists store_access cascade;
drop sequence if exists store_access_seq;