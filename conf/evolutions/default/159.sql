# --- !Ups

create table voucher_purchase_history (
                                          id				bigint not null,
                                          is_deleted		boolean,
                                          user_id			bigint not null,
                                          voucher_id		bigint not null,
                                          price			numeric,
                                          created_at		timestamp,
                                          updated_at		timestamp,
                                          constraint pk_voucher_purchase_record primary key (id))
;

create table voucher_available_store (
                                         id				bigint not null,
                                         is_deleted		boolean,
                                         voucher_id 		bigint not null,
                                         store_id		bigint not null,
                                         created_at		timestamp,
                                         updated_at		timestamp,
                                         constraint pk_voucher_store	primary key (id)
);

alter table voucher_available_store add constraint fk_voucher_available_store_voucher_id foreign key (voucher_id) references voucher_merchant_new (id);
alter table voucher_available_store add constraint fk_voucher_available_store_store_id foreign key (store_id) references store (id);
alter table voucher_purchase_history add constraint fk_voucher_purchase_user_id foreign key (user_id) references "member"(id);
alter table voucher_purchase_history add constraint fk_voucher_purchase_voucher_id foreign key (voucher_id) references voucher_merchant_new(id);


create index ix_voucher_available_store_voucher_id on voucher_available_store (store_id);
create index ix_voucher_available_store_store_id on voucher_available_store (voucher_id);
create index ix_voucher_purchase_history_voucher_id on voucher_purchase_history (voucher_id);
create index ix_voucher_purchase_history_user_id on voucher_purchase_history (user_id);

create sequence voucher_purchase_history_seq;
create sequence voucher_available_store_seq;

alter table voucher_user add created_at timestamp;
alter table voucher_user add updated_at timestamp;

# --- !Downs

drop table if exists voucher_purchase_history cascade;
drop table if exists voucher_available_store cascade;

drop sequence if exists voucher_purchase_history_seq;
drop sequence if exists voucher_available_store_seq;

