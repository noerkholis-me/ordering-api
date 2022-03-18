# --- !Ups

create table user_merchant (
   id                        bigint not null,
   is_deleted                boolean,
   password                  varchar(255),
   first_name                varchar(255),
   last_name                 varchar(255),
   email                     varchar(255),
   full_name                 varchar(255),
   phone                     varchar(255),
   gender                    varchar(1),
   birth_date                timestamp,
   activation_code           varchar(255),
   is_active                 boolean,
   role_id                   bigint,
   merchant_id               bigint,
   created_at                timestamp not null,
   updated_at                timestamp not null,
   constraint uq_user_merchant_email unique (email),
   constraint pk_user_merchant primary key (id))
;

create sequence user_merchant_seq;

alter table user_merchant add constraint fk_user_merchant_role_merchant_206 foreign key (role_id) references role_merchant (id);
create index ix_user_merchant_role_merchant_206 on user_merchant (role_id);

alter table user_merchant add constraint fk_user_merchant_merchant_206 foreign key (merchant_id) references merchant (id);
create index ix_user_merchant_merchant_206 on user_merchant (merchant_id);

# --- !Downs

drop table if exists user_merchant cascade;

drop sequence if exists user_merchant_seq;