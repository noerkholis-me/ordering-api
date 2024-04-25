# --- !Ups

alter table product_store add column is_publish boolean default false;


# --- !Downs

alter table product_store drop column if exists is_publish;