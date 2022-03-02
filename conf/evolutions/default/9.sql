# --- !Ups

ALTER TABLE member ADD COLUMN referral_code varchar(255) null;

CREATE TABLE member_referral(
	id							bigint not null,
  	is_deleted					boolean,
  	member_id					bigint not null,
	referral_id					bigint not null,
  	created_at                	timestamp not null,
  	updated_at                	timestamp not null,
  	constraint pk_member_referral primary key (id));

ALTER TABLE member_referral add constraint fk_member foreign key (member_id) references member(id);
ALTER TABLE member_referral add constraint fk_member_referral foreign key (referral_id) references member(id);

CREATE SEQUENCE member_referral_seq
INCREMENT 1
START 1;

# --- !Downs

DROP SEQUENCE IF EXISTS  member_referral_seq;
drop table if exists member_referral;

ALTER TABLE member DROP COLUMN if exists referral_code; 