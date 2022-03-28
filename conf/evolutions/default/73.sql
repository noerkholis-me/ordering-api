# --- !Ups
alter table store add column merchant_id bigint;
alter table store add column store_qr_code text;
alter table store alter column store_code type varchar(8);
alter table store rename column status to is_active;

alter table store add constraint fk_store_merchant_201 foreign key (merchant_id) references merchant (id);

create index ix_store_merchant_201 on store (merchant_id);

# --- !Downs
alter table store drop column if exists merchant_id;
alter table store drop column if exists store_qr_code;