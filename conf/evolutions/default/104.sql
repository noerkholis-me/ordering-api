# --- !Ups

create table store_access_detail (
      id                                bigint,
      store_access_id                   bigint,
      store_id                          bigint,
      is_deleted                        boolean,
      created_at                        timestamp not null,
      updated_at                        timestamp not null,
      constraint pk_store_access_detail primary key (id)
);

create sequence store_access_detail_seq;
alter table store_access_detail drop constraint if exists fk_store_access_id;
alter table store_access_detail add constraint fk_store_access_id foreign key (store_access_id) references store_access (id);
alter table store_access_detail drop constraint if exists fk_store_id;
alter table store_access_detail add constraint fk_store_id foreign key (store_id) references store (id);
create index idx_store_access_detail_store_access_id on store_access_detail (store_access_id);
create index idx_store_access_detail_store_id on store_access_detail (store_id);

alter table store_access drop constraint if exists fk_store_id;
alter table store_access drop column if exists store_id;


# --- !Downs

drop table if exists store_access_detail cascade;
drop sequence if exists store_access_detail_seq;