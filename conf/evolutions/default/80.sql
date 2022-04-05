# --- !Ups
ALTER TABLE sub_category_merchant add column sequence int;

# --- !Downs
alter table sub_category_merchant drop column if exists sequence;