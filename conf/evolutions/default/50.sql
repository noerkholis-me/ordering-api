
# --- !Ups

alter table product add column product_length double precision default 1.0;
alter table product add column product_width double precision default 1.0;
alter table product add column product_height double precision default 1.0;
alter table product add column product_weight double precision default 1.0;

create table syncshipper (
 id                        bigint not null,
 is_deleted                boolean,
 status                    boolean,
 user_id		   bigint,
 sync_name                 varchar(4) not null,
 sync_description          TEXT,
 created_at                timestamp not null,
 updated_at                timestamp not null,
 constraint pk_syncshipper primary key (id))
;

create sequence syncshipper_seq;


create table shipper_province (
 id                        bigint not null,
 is_deleted                boolean,
 status                    boolean,
 shipper_province_name     varchar(255),
 created_at                timestamp not null,
 updated_at                timestamp not null,
 user_id                   bigint,
 constraint pk_shipper_province primary key (id))
;


create table shipper_city (
 id                        bigint not null,
 is_deleted                boolean,
 status                    boolean,
 shipper_city_name         varchar(255),
 province_id		   bigint,
 province_name		   varchar(255),
 created_at                timestamp not null,
 updated_at                timestamp not null,
 user_id                   bigint,
 constraint pk_shipper_city primary key (id))
;


create table shipper_suburb (
 id                        bigint not null,
 is_deleted                boolean,
 status                    boolean,
 name                	   varchar(255),
 alias			   varchar(255),
 city_id		   bigint,
 created_at                timestamp not null,
 updated_at                timestamp not null,
 user_id                   bigint,
 constraint pk_shipper_suburb primary key (id))
;


create table shipper_area (
 id                        bigint not null,
 is_deleted                boolean,
 status                    boolean,
 name                	   varchar(255),
 post_code		   varchar(10),
 alias			   varchar(25),
 suburb_id		   bigint,
 created_at                timestamp not null,
 updated_at                timestamp not null,
 user_id                   bigint,
 constraint pk_shipper_area primary key (id))
;


# --- !Downs

drop table if exists syncshipper cascade;
drop sequence if exists syncshipper_seq;
drop table if exists shipper_area cascade;
drop table if exists shipper_suburb cascade;
drop table if exists shipper_city cascade;
drop table if exists shipper_province cascade;