# --- !Ups
create table category_loyaltypoint (
	id                      bigint not null,
	loyalty_usage_type 		integer,
	loyalty_usage_value		float,
	max_loyalty_usage_value	float,
	cashback_type 			integer,
	cashback_value 			float,
	max_cashback_value 		float,
	category_id				bigint,
	is_deleted				boolean,
  	created_at              timestamp not null,
  	updated_at              timestamp not null,
	constraint pk_category_loyaltypoint primary key (id)
);

alter table category_loyaltypoint
add constraint fk_category_loyaltypoint foreign key (category_id) references category(id);

create sequence category_loyaltypoint_seq;

alter table sales_order_detail add column loyalty_eligible_use bigint null;
alter table sales_order_detail add column loyalty_eligible_earn bigint null;

# --- !Downs
alter table sales_order_detail drop column if exists loyalty_eligible_earn;
alter table sales_order_detail drop column if exists loyalty_eligible_use;

drop sequence if exists category_loyaltypoint_seq;

alter table category_loyaltypoint
drop constraint fk_category_loyaltypoint;

drop table if exists category_loyaltypoint cascade;