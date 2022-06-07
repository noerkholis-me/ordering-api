# --- !Ups

create table bank_account_merchant (
        id                                  bigint,
        bank_name                           varchar(10),
        account_number                      varchar(100),
        account_name                        varchar(100),
        is_primary                          boolean,
        merchant_id                         bigint,
        is_deleted                          boolean,
        created_at                          timestamp not null,
        updated_at                          timestamp not null,
        constraint pk_bank_account_merchant primary key (id)
);

create sequence bank_account_merchant_seq;
alter table bank_account_merchant drop constraint if exists fk_bank_account_merchant_id;
alter table bank_account_merchant add constraint fk_bank_account_merchant_id foreign key (merchant_id) references merchant (id);
create index idx_bank_account_merchant_id on bank_account_merchant (merchant_id);


# --- !Downs

drop table if exists bank_account_merchant cascade;
drop sequence if exists bank_account_merchant_seq;