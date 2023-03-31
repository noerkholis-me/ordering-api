# --- !Ups

alter table order_payment add column bank_code varchar(255) default null;


# --- !Downs

alter table order_payment drop column if exists bank_code;
