# --- !Ups


create table attribute_group (
  id                        bigint not null,
  created_at                timestamp,
  updated_at                timestamp,
  name                 		varchar(255),
  constraint uq_attribute_group_name unique (name),
  constraint pk_attribute_group primary key (id)
 )
;

create table attrib (
  id                        bigint not null,
  created_at                timestamp,
  updated_at                timestamp,
  attribute_group_id        bigint,
  name                      varchar(255),
  description               TEXT,
  input_type				varchar(20),
  show_in					varchar(20),
  mandatory					boolean,
  constraint ck_attrib_input_type check (input_type in ('MULTIPLE_CHOICE', 'SHORT_TEXT', 'LONG_TEXT', 'FILE')),
  constraint ck_attrib_show_in check (show_in in ('MASTER_PRODUCT', 'PRODUCT_DETAIL')),
  constraint pk_attrib primary key (id)
 )
;

create sequence attribute_group_seq;
create sequence attrib_seq;

alter table attrib add constraint fk_attrib_attribute_group foreign key (attribute_group_id) references attribute_group (id);
create index idx_attrib_attribute_group on attrib (attribute_group_id);


# --- !Downs

drop table if exists attribute_group cascade;
drop table if exists attrib cascade;

drop sequence if exists attribute_group_seq;
drop sequence if exists attrib_seq;

