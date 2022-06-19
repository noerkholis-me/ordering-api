# --- !Ups

alter table user_merchant add column reset_token varchar (255) default null;
alter table user_merchant add column reset_time bigint;
alter table finance_withdraw add column approval_date timestamp;
alter table finance_withdraw add column approved_by varchar (50);


# --- !Downs

alter table user_merchant drop column if exists reset_token;
alter table user_merchant drop column if exists reset_time;
alter table finance_withdraw drop column if exists approval_date;
alter table finance_withdraw drop column if exists approved_by;