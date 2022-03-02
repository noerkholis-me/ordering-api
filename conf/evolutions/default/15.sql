# --- !Ups

CREATE TABLE product_price(
	id							bigint not null,
  	is_deleted					boolean,
	product_id					bigint not null,
	start_date					timestamp not null,
	end_date					timestamp not null,
	sale_price					float not null,
  	created_at                	timestamp not null,
  	updated_at                	timestamp not null,
  	is_active					boolean,
  	override_type				int,
  	discount_percentage			float,
  	discount_nominal			float,
  	constraint pk_product_price primary key (id));

ALTER TABLE product_price add constraint fk_product foreign key (product_id) references product(id);

CREATE SEQUENCE product_price_seq
INCREMENT 1
START 1;

ALTER TABLE mobile_version
add column mobile_version_ios integer default 0;

# --- !Downs

DROP SEQUENCE IF EXISTS  product_price_seq;
drop table if exists product_price;

ALTER TABLE mobile_version
drop column mobile_version_ios;