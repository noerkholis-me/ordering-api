# --- !Ups

alter table store add column is_publish boolean default true;


# --- !Downs

alter table store drop column if exists is_publish;