# --- !Ups

alter table finance_withdraw add column request_by  varchar (100);


# --- !Downs

drop column if exists request_by;