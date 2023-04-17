# --- !Ups

CREATE TABLE voucher_merchant_new (
    id                          bigint not null,
    available                   boolean,
    is_deleted                  boolean,
    value                       INT NOT NULL,
    purchase_price              INT NOT NULL,
    expiry_days                 INT not NUll,
    code                        VARCHAR(45) NOT NULL,
    tittle                      VARCHAR(128) NOT NULL,
    description                  text,
    value_text                  VARCHAR(50) NOT NULL,
    merchant_id                 bigint not null,
    created_at                  timestamp not null,
    updated_at                  timestamp not null,
    voucher_type                VARCHAR(20),
    constraint pk_voucher_merchant_new primary key (id)
);



alter table voucher_merchant_new add constraint fk_voucher_merchant_new_merchant_id foreign key (merchant_id) references merchant (id);
create index ix_voucher_merchant_new_merchant_id on voucher_merchant_new (merchant_id);

create sequence voucher_merchant_new_seq;

# --- !Downs

drop table if exists voucher_merchant_new cascade;
drop sequence if exists voucher_merchant_new_seq;