# --- !Ups
ALTER TABLE merchant ADD COLUMN province_id bigint;
ALTER TABLE merchant ADD COLUMN city_id bigint;
ALTER TABLE merchant ADD COLUMN suburb_id bigint;
ALTER TABLE merchant ADD COLUMN area_id bigint;

alter table merchant add constraint fk_merchant_province_71 foreign key (province_id) references shipper_province (id);
create index ix_merchant_province_71 on merchant (province_id);
alter table merchant add constraint fk_merchant_city_72 foreign key (city_id) references shipper_city (id);
create index ix_merchant_city_72 on merchant (city_id);
alter table merchant add constraint fk_merchant_suburb_73 foreign key (suburb_id) references shipper_suburb (id);
create index ix_merchant_suburb_73 on merchant (suburb_id);
alter table merchant add constraint fk_merchant_area_74 foreign key (area_id) references shipper_area (id);
create index ix_merchant_area_74 on merchant (area_id);

# --- !Downs
alter table merchant drop column if exists province_id;
alter table merchant drop column if exists city_id;
alter table merchant drop column if exists suburb_id;
alter table merchant drop column if exists area_id;