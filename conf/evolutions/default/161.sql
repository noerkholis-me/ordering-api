# --- !Ups
create table delivery_setting (
  id                        bigint not null,
  delivery_method           varchar(255),
  normal_price              numeric,
  normal_price_max_range    int,
  basic_price               numeric,
  basic_price_max_range     int,
  is_active_base_price      boolean,
  is_shipper                boolean,
  store_id                  bigint,
  merchant_id				bigint,
  is_deleted                boolean,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_delivery_setting primary key (id))
;

create sequence delivery_setting_seq;

ALTER TABLE delivery_setting ADD CONSTRAINT fk_store_id FOREIGN KEY (store_id) REFERENCES store (id);
ALTER TABLE delivery_setting ADD CONSTRAINT fk_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchant (id);

# --- !Downs
drop table if exists delivery_setting;

drop sequence if exists delivery_setting_seq;
