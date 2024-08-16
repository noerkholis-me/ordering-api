# --- !Ups

alter table orders add column voucher_code varchar(100) default null;


# --- !Downs

alter table orders drop column if exists voucher_code;