# --- !Ups

CREATE TABLE pick_up_point(
	id							bigint not null,
  	is_deleted					boolean,
  	merchant_id					bigint null,
  	name						varchar(255) not null,
  	address						TEXT not null,
  	contact						varchar(255),
  	duration					bigint not null,
  	latitude					float,
  	longitude					float,
  	created_at                	timestamp not null,
  	updated_at                	timestamp not null,
  	constraint pk_pick_up_point primary key (id));

ALTER TABLE pick_up_point ADD CONSTRAINT fk_merchant FOREIGN KEY (merchant_id) REFERENCES merchant(id);

CREATE SEQUENCE pick_up_point_seq
INCREMENT 1
START 1;

# --- !Downs

DROP SEQUENCE if EXISTS pick_up_point_seq;
DROP TABLE if EXISTS pick_up_point;