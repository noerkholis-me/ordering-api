# --- !Ups
create table qr_group (
  id                        bigint not null,
  is_deleted                boolean,
  group_name                varchar(50),
  group_logo                text,
  address_type              boolean,
  address				    text,
  phone			            varchar(20),
  province_id				bigint,
  shipper_city_id			bigint,
  suburb_id				    bigint,
  area_id					bigint,
  url_gmap				    text,
  longitude				    double precision,
  latitude				    double precision,
  group_code                varchar(8) not null,
  group_qr_code             text,
  merchant_id				bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_qr_group primary key (id))
;

create table qr_group_store (
  id                        bigint not null,
  is_deleted                boolean,
  store_id                  bigint,
  qr_group_id				bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_qr_group_store primary key (id))
;

create sequence qr_group_seq;
create sequence qr_group_store_seq;

ALTER TABLE qr_group ADD CONSTRAINT fk_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchant (id);
ALTER TABLE qr_group_store ADD CONSTRAINT fk_store_id FOREIGN KEY (store_id) REFERENCES store (id);
ALTER TABLE qr_group_store ADD CONSTRAINT fk_qr_group_id FOREIGN KEY (qr_group_id) REFERENCES qr_group (id);

# --- !Downs
drop table if exists qr_group;
drop table if exists qr_group_store;

drop sequence if exists qr_group_seq;
drop sequence if exists qr_group_store_seq;
