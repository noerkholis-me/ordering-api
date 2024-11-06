# --- !Ups

alter table order_detail_status add column is_deleted boolean default false;


# --- !Downs

alter table order_detail_status drop column if exists is_deleted;