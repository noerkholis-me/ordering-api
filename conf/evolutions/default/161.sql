# --- !Ups
CREATE TABLE store_ratings(
                               id						bigint not null,
                               store_id					bigint not null,
                               member_id			    bigint not null,
                               feedback                 text,
                               rate                     float,
                               is_deleted				boolean,
                               created_at                timestamp not null,
                               updated_at                timestamp not null,
                               constraint pk_store_ratings primary key (id));

ALTER TABLE store_ratings add constraint fk_store_ratings_store_id foreign key (store_id) references store(id);
ALTER TABLE store_ratings add constraint fk_store_ratings_member_id foreign key (member_id) references member(id);

CREATE SEQUENCE store_ratings_seq
    INCREMENT 1
START 1;

CREATE TABLE product_ratings(
                              id						bigint not null,
                              store_id					bigint not null,
                              member_id			        bigint not null,
                              order_number              varchar(255),
                              product_merchant_id       bigint not null,
                              feedback                  text,
                              rate                      float,
                              is_deleted				boolean,
                              created_at                timestamp not null,
                              updated_at                timestamp not null,
                              constraint pk_product_ratings primary key (id));

ALTER TABLE product_ratings add constraint fk_product_ratings_store_id foreign key (store_id) references store(id);
ALTER TABLE product_ratings add constraint fk_product_ratings_member_id foreign key (member_id) references member(id);
ALTER TABLE product_ratings add constraint fk_product_ratings_product_merchant_id foreign key (product_merchant_id) references product_merchant(id);

CREATE SEQUENCE product_ratings_seq
    INCREMENT 1
START 1;


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

DROP SEQUENCE IF EXISTS  store_ratings_seq;
drop table if exists store_ratings;
drop table if exists delivery_setting;

drop sequence if exists delivery_setting_seq;
