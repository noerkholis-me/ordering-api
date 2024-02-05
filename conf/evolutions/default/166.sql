# --- !Ups

alter table order_payment add column delivery_fee                  numeric;


# --- !Downs

drop column if exist delivery_fee;

