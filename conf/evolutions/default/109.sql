# --- !Ups

alter table finance_transaction alter id set default nextval('finance_transaction_seq');
alter table finance_withdraw alter id set default nextval('finance_withdraw_seq');


# --- !Downs