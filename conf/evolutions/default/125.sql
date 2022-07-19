# --- !Ups
alter table merchant
    add column is_kiosk                                     boolean default false,
    add column is_pos                                       boolean default false,
    add column is_cash                                      boolean default false,
    add column type_cash                                    varchar(100),
    add column is_debit_credit                              boolean default false,
    add column type_debit_credit                            varchar(100),
    add column is_qris                                      boolean default false,
    add column type_qris                                    varchar(100),
    add column is_mobile_qr                                 boolean default false;


# --- !Downs

alter table merchant drop column if exists is_kiosk;
alter table merchant drop column if exists is_pos;
alter table merchant drop column if exists is_cash;
alter table merchant drop column if exists type_cash;
alter table merchant drop column if exists is_debit_credit;
alter table merchant drop column if exists type_debit_credit;
alter table merchant drop column if exists is_qris;
alter table merchant drop column if exists type_qris;
alter table merchant drop column if exists is_mobile_qr;