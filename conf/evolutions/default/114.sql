# --- !Ups

alter table finance_withdraw add column account_name varchar (100);
alter table finance_withdraw add column bank_name varchar (100);


# --- !Downs

alter table finance_withdraw drop column if exists account_name;
alter table finance_withdraw drop column if exists bank_name;