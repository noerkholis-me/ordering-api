# --- !Ups

alter table brand_merchant
add column brand_type varchar(50),
add column brand_description varchar(50),
add column icon_web text,
add column icon_mobile text;