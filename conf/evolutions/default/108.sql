# --- !Ups

create table finance_transaction (
       id                                   bigint,
       event_id                             varchar(255),
       reference_number                     varchar(50),
       date                                 timestamp,
       transaction_type                     varchar (20),
       status                               varchar (20),
       amount                               numeric,
       store_id                             bigint,
       is_deleted                           boolean,
       created_at                           timestamp not null,
       updated_at                           timestamp not null,
       constraint fk_finance_transaction    primary key (id)
);

create sequence finance_transaction_seq;
alter table finance_transaction drop constraint if exists fk_transaction_store_id;
alter table finance_transaction add constraint fk_transaction_store_id foreign key (store_id) references store (id);
create index idx_transaction_store_id on finance_transaction (store_id);

create table finance_withdraw (
         id                                   bigint,
         event_id                             varchar(255),
         request_number                       varchar(255),
         date                                 timestamp,
         status                               varchar (20),
         amount                               numeric,
         store_id                             bigint,
         is_deleted                           boolean,
         created_at                           timestamp not null,
         updated_at                           timestamp not null,
         constraint fk_finance_withdraw    primary key (id)
);

create sequence finance_withdraw_seq;
alter table finance_withdraw drop constraint if exists fk_withdraw_store_id;
alter table finance_withdraw add constraint fk_withdraw_store_id foreign key (store_id) references store (id);
create index idx_withdraw_store_id on finance_withdraw (store_id);


# --- !Downs

drop table if exists finance_transaction cascade;
drop sequence if exists finance_transaction_seq;
drop table if exists finance_withdraw cascade;
drop sequence if exists finance_withdraw_seq;