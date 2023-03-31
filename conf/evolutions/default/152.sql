# --- !Ups

alter table merchant add column global_store_qr_group boolean default false;
update merchant set global_store_qr_group = false;


# --- !Downs

alter table merchant drop column if exists global_store_qr_group;
