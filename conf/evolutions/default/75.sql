# --- !Ups
ALTER TABLE merchant
RENAME province_id TO district_id;
ALTER TABLE merchant
RENAME city_id TO township_id;
ALTER TABLE merchant
RENAME suburb_id TO region_id;
ALTER TABLE merchant
RENAME area_id TO village_id;

ALTER TABLE merchant
ADD COLUMN province_id bigint,
ADD COLUMN city_id bigint,
ADD COLUMN suburb_id bigint,
ADD COLUMN area_id bigint;

alter table merchant add constraint fk_merchant_province_71 foreign key (province_id) references shipper_province (id);
create index ix_merchant_province_71 on merchant (province_id);
alter table merchant add constraint fk_merchant_city_72 foreign key (city_id) references shipper_city (id);
create index ix_merchant_city_72 on merchant (city_id);
alter table merchant add constraint fk_merchant_suburb_73 foreign key (suburb_id) references shipper_suburb (id);
create index ix_merchant_suburb_73 on merchant (suburb_id);
alter table merchant add constraint fk_merchant_area_74 foreign key (area_id) references shipper_area (id);
create index ix_merchant_area_74 on merchant (area_id);

alter table merchant add constraint fk_merchant_district_71 foreign key (district_id) references district (id);
create index ix_merchant_district_71 on merchant (district_id);
alter table merchant add constraint fk_merchant_township_72 foreign key (township_id) references township (id);
create index ix_merchant_township_72 on merchant (township_id);
alter table merchant add constraint fk_merchant_region_73 foreign key (region_id) references region (id);
create index ix_merchant_region_73 on merchant (region_id);
alter table merchant add constraint fk_merchant_village_74 foreign key (village_id) references village (id);
create index ix_merchant_village_74 on merchant (village_id);