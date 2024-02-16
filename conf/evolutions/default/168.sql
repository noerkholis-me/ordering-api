# --- !Ups

alter table product_merchant add column rating               numeric;


# --- !Downs

alter table product_merchant drop column if exist rating;