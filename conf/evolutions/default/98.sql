# --- !Ups

alter table orders        add column order_queue                integer;
alter table payment_detail add column account_number            varchar (50);
alter table payment_detail add column payment_id                varchar (100);
alter table order_payment add column tax_percentage             DOUBLE PRECISION;
alter table order_payment add column service_percentage         DOUBLE PRECISION;
alter table order_payment add column tax_price                  numeric;
alter table order_payment add column service_price              numeric;
alter table order_payment add column payment_fee_type           varchar(10);
alter table order_payment add column payment_fee_customer       numeric;
alter table order_payment add column payment_fee_owner          numeric;


# --- !Downs

drop column if exists order_queue;
drop column if exists account_number;
drop column if exists payment_id;
drop column if exist tax_percentage;
drop column if exist service_percentage;
drop column if exist tax_price;
drop column if exist service_price;
drop column if exist payment_fee_type;
drop column if exist payment_fee_customer;
drop column if exist payment_fee_owner;
