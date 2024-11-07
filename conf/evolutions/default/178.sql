# --- !Ups

alter table order_detail_status add column updated_at TIMESTAMP default null;


# --- !Downs

alter table order_detail_status drop column if exists updated_at;