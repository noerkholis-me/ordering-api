# --- !Ups
alter table store add column merchant_id bigint;

alter table store add constraint fk_store_merchant_201 foreign key (merchant_id) references merchant (id);

create index ix_store_merchant_201 on store (merchant_id);

# --- !Downs
alter table store drop column if exists merchant_id;