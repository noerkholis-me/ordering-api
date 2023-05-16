# --- !Ups

CREATE TABLE voucher_how_to_use (
    id                          bigint not null,
    is_deleted                  boolean,
    content                     text,
    created_at                  timestamp not null,
    updated_at                  timestamp not null,
    voucher_id                  bigint not null,
    constraint pk_voucher_how_to_use primary key (id)
);

alter table voucher_how_to_use add constraint fk_voucher_how_to_use_voucher_id foreign key (voucher_id) references voucher_merchant_new (id);
create index ix_voucher_how_to_use_voucher_id on voucher_how_to_use (voucher_id);

create sequence voucher_how_to_use_seq;

# --- !Downs

drop table if exists voucher_how_to_use cascade;
drop sequence if exists voucher_how_to_use_seq;