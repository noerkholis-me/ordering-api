# --- !Ups
create table master_size_in_carat_custom_diamond (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  description               varchar(255),
  image_name                varchar(255),
  url                       varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_master_size_in_carat_custom_diamond primary key (id))
;
create table master_color_custom_diamond (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  color_type                varchar(255),
  description               varchar(255),
  image_name                varchar(255),
  url                       varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_master_color_custom_diamond primary key (id))
;
create table master_clarity_custom_diamond (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  description               varchar(255),
  image_name                varchar(255),
  url                       varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_master_clarity_custom_diamond primary key (id))
;
ALTER TABLE banner ADD COLUMN custom_diamond boolean;

create sequence master_size_in_carat_custom_diamond_seq;

create sequence master_color_custom_diamond_seq;

create sequence master_clarity_custom_diamond_seq;

# --- !Downs
ALTER TABLE banner DROP COLUMN if exists custom_diamond;

drop table if exists master_size_in_carat_custom_diamond;

drop sequence if exists master_size_in_carat_custom_diamond_seq;

drop table if exists master_color_custom_diamond;

drop sequence if exists master_color_custom_diamond_seq;

drop table if exists master_clarity_custom_diamond;

drop sequence if exists master_clarity_custom_diamond_seq;