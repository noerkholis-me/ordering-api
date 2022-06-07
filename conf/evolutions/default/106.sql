# --- !Ups

alter table loyalty_point_merchant drop column if exists subs_category_id;
alter table loyalty_point_merchant add column subs_category_id bigint;

alter table loyalty_point_merchant drop constraint if exists fk_loyalty_point_subs_category_id;
alter table loyalty_point_merchant add constraint fk_loyalty_point_subs_category_id foreign key (subs_category_id) references subs_category_merchant (id);
create index idx_loyalty_point_merchant_subs_category_id on loyalty_point_merchant (subs_category_id);

# --- !Downs