# --- !Ups

alter table order_payment add column delivery_fee                  numeric;


# --- !Downs

alter table order_payment drop column if exists delivery_fee;

