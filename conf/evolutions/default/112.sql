# --- !Ups

alter table merchant_log add column user_merchant_id bigint default null;

alter table merchant_log drop constraint if exists fk_merchant_user_merchant_id;
alter table merchant_log add constraint fk_merchant_user_merchant_id foreign key (user_merchant_id) references user_merchant (id);
create index idx_merchant_user_merchant_id on merchant_log (user_merchant_id);


# --- !Downs

alter table merchant_log drop column if exists user_merchant_id cascade;