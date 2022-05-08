# --- !Ups
create table fee_setting_merchant (
        id                                  bigint,
        date                                timestamp,
        tax                                 DOUBLE PRECISION,
        service                             DOUBLE PRECISION,
        platform_fee_type                   varchar(20),
        payment_fee_type                    varchar(20),
        platform_fee                        numeric,
        payment_fee                         numeric,
        updated_by                          varchar(20),
        merchant_id                         bigint,
        is_deleted                          boolean,
        created_at                          timestamp not null,
        updated_at                          timestamp not null,
        constraint pk_fee_setting_merchant  primary key (id)
);

create sequence fee_setting_merchant_seq;
alter table fee_setting_merchant drop constraint if exists fk_fee_setting_merchant_id;
alter table fee_setting_merchant add constraint fk_fee_setting_merchant_id foreign key (merchant_id) references merchant (id);
create index idx_fee_setting_merchant_id on fee_setting_merchant (merchant_id);

# --- !Downs

drop table if exists fee_setting_merchant cascade;
drop sequence if exists fee_setting_merchant_seq;