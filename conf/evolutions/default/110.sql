# --- !Ups

alter table store add column active_balance    numeric;
alter table merchant add column total_active_balance numeric;
alter table finance_withdraw add column account_number  varchar (50);


# --- !Downs

drop column if exists active_balance;
drop column if exists total_active_balance;
drop column if exists account_number;