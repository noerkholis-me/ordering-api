# --- !Ups
CREATE TABLE merchant_payment  (
      id                                bigint,
      merchant_id                       bigint,
      payment_method_id                 bigint,
      device                            varchar(25),
      is_active                         boolean,
      is_deleted                        boolean,
      created_at                        timestamp not null,
      updated_at                        timestamp not null,
      constraint pk_merchant_payment primary key (id)
);

create sequence merchant_payment_seq;
alter table merchant_payment drop constraint if exists fk_merchant_payment_merchant_id;
alter table merchant_payment add constraint fk_merchant_payment_merchant_id foreign key (merchant_id) references merchant (id);
create index idx_m_pay_merchant_id on merchant_payment (merchant_id);
alter table merchant_payment drop constraint if exists fk_merchant_payment_method_id;
alter table merchant_payment add constraint fk_merchant_payment_method_id foreign key (payment_method_id) references payment_method (id);
create index idx_m_pay_payment_method_id on merchant_payment (payment_method_id);


# --- !Downs

ALTER TABLE merchant DROP COLUMN if exists is_cash;
ALTER TABLE merchant DROP COLUMN if exists is_debit_credit;
ALTER TABLE merchant DROP COLUMN if exists is_qris;
ALTER TABLE merchant DROP COLUMN if exists type_cash;
ALTER TABLE merchant DROP COLUMN if exists type_debit_credit;
ALTER TABLE merchant DROP COLUMN if exists type_qris;