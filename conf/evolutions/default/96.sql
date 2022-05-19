# --- !Ups

create table payment_method (
     id                         bigint,
     payment_code               varchar(20),
     payment_name               varchar(100),
     payment_fee_price          numeric,
     payment_fee_percentage     DOUBLE PRECISION,
     is_available               boolean,
     is_active                  boolean,
     is_deleted                 boolean,
     created_at                 timestamp not null,
     updated_at                 timestamp not null,
     constraint pk_payment_method primary key (id)
);

create sequence payment_method_seq;

# --- !Downs

drop table if exists payment_method cascade;
drop sequence if exists payment_method_seq;