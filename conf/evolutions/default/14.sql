# --- !Ups

CREATE TABLE bag(
	id							bigint not null,
  	is_deleted					boolean,
  	member_id					bigint not null,
	product_detail_variance_id	bigint not null,
	quantity					bigint not null,
	status						varchar(10),
  	created_at                	timestamp not null,
  	updated_at                	timestamp not null,
  	constraint pk_bag primary key (id));

ALTER TABLE bag add constraint fk_member foreign key (member_id) references member(id);
ALTER TABLE bag add constraint fk_product_detail_variance foreign key (product_detail_variance_id) references product_detail_variance(id);

CREATE SEQUENCE bag_seq
INCREMENT 1
START 1;

# --- !Downs

DROP SEQUENCE IF EXISTS  bag_seq;
drop table if exists bag;