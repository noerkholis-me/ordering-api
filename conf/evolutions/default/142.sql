# --- !Ups

alter table fee_setting_merchant add column store_id bigint default null;

alter table fee_setting_merchant add constraint fk_fee_setting_merchant_store_id foreign key (store_id) references store (id);
create index idx_fee_setting_merchant_store_id on fee_setting_merchant (store_id);


# --- !Downs

alter table fee_setting_merchant drop column if exists store_id cascade;