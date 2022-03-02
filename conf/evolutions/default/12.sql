# --- !Ups
DROP TABLE IF EXISTS member_referral;
ALTER TABLE member DROP COLUMN IF EXISTS referral_code;

ALTER TABLE member ADD COLUMN referral_code varchar(255) null;

CREATE TABLE member_referral(
	id							serial,
  	is_deleted					boolean,
  	member_id					bigint not null,
	referral_id					bigint not null,
  	created_at                	timestamp not null,
  	updated_at                	timestamp not null,
  	constraint pk_member_referral primary key (id));

CREATE SEQUENCE member_referral_seqs
INCREMENT 1
START 1;


# --- !Downs

DROP SEQUENCE IF EXISTS member_referral_seqs;

ALTER TABLE IF EXISTS member_referral 
  DROP CONSTRAINT IF EXISTS fk_member;
  
ALTER TABLE IF EXISTS member_referral 
  DROP CONSTRAINT IF EXISTS fk_member_referral;