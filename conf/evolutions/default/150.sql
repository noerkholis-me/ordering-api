# --- !Ups
create table multi_store (
  id                        bigint not null,
  is_deleted                boolean,
  address_type              varchar(50),
  store_address				text,
  store_phone			    varchar(20),
  store_gmap				text,
  store_long				double precision,
  store_lat				    double precision,
  multi_store_code          varchar(8),
  multi_store_qr_code		text,
  merchant_id				bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_multi_store_merchant primary key (id))
;

create sequence multi_store_seq;

ALTER TABLE multi_store ADD CONSTRAINT fk_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchant (id);

# --- !Downs
drop table if exists multi_store;

drop sequence if exists multi_store_seq;
