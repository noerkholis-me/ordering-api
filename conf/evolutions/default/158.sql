# --- !Ups

create table voucher_user (
	id				bigint not null,
	is_deleted		boolean,
	user_id			bigint not null,
	voucher_id		bigint not null,
	available		boolean,
	constraint pk_voucher_user primary key (id)
);
alter table voucher_user add constraint fk_voucher_user_user_id foreign key (user_id) references "member" (id);
alter table voucher_user add constraint fk_voucher_user_voucher_id foreign key (voucher_id) references voucher_merchant_new (id);

create index ix_voucher_user_voucher_id on voucher_user (voucher_id);
create index ix_voucher_user_user_id on voucher_user (user_id);

create sequence voucher_user_seq;

alter table orders add discount_amount numeric;

# --- !Downs

drop table if exists voucher_user cascade;
drop sequence if exists voucher_user_seq;

alter table orders drop column if exists discount_amount;
