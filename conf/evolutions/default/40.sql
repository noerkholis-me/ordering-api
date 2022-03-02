# --- !Ups

create table s_order_payment (
  id                        bigint not null,
  is_deleted                boolean,
  order_id            		bigint,
  confirm_at                timestamp,
  void_at                   timestamp,
  confirm_by_id             bigint,
  total_transfer            float,
  invoice_no                varchar(255),
  debit_account_name        varchar(255),
  debit_account_number      varchar(255),
  image_url                 varchar(255),
  comments                  varchar(255),
  status                    varchar(255),
  transaction_id            varchar(255),
  eci_code                  varchar(255),
  payment_instalment        varchar(255),
  va_number                 varchar(255),
  company_code              varchar(255),
  settlement                boolean,
  payment_type              varchar(255),
  bank                      varchar(255),
  card_type                 varchar(255),
  instalment_cost           float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_s_order_payment_invoice_n unique (invoice_no),
  constraint pk_s_order_payment primary key (id))
;

create sequence s_order_payment_seq;

alter table s_order_payment add constraint fk_s_order_payment foreign key (order_id) references s_order (id);
create index ix_s_order_payment_sales on s_order_payment (order_id);
alter table s_order_payment add constraint fk_s_order_payment_confi foreign key (confirm_by_id) references user_cms (id);
create index ix_s_order_payment_confi on s_order_payment (confirm_by_id);


# --- !Downs

drop table if exists s_order_payment cascade;
drop sequence if exists s_order_payment_seq;
