# --- !Ups

create table role_merchant (
   id                        bigint not null,
   is_deleted                boolean,
   name                      varchar(255),
   description               varchar(255),
   key                       varchar(255),
   merchant_id               bigint,
   created_at                timestamp not null,
   updated_at                timestamp not null,
   constraint pk_role_merchant primary key (id))
;

create sequence role_merchant_seq;

alter table role_merchant add constraint fk_role_merchant_merchant_206 foreign key (merchant_id) references merchant (id);
create index ix_role_merchant_merchant_206 on role_merchant (merchant_id);

# --- !Downs

drop table if exists role_merchant cascade;

drop sequence if exists role_merchant_seq;