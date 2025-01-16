# --- !Ups

alter table store add column is_new_order BOOLEAN default false;


# --- !Downs

alter table store drop column if exists is_new_order;