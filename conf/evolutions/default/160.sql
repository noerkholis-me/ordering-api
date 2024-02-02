# --- !Ups

alter table store add column store_qr_code_static text;


# --- !Downs

alter table store drop column if exists store_qr_code_static;
