# --- !Ups

alter table order_detail        add column sub_total        numeric;
alter table order_detail        add column is_customizable  boolean;
alter table order_detail_add_on add column sub_total        numeric;

# --- !Downs
drop column if exists sub_total;
drop column if exists is_customizable;
drop column if exists sub_total;