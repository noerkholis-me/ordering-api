# --- !Ups

create table member_address (
  id                        bigint not null,
  is_deleted                boolean,
  name                		varchar(255),
  note        				varchar(255),
  latitude      			varchar(255),
  longitude                 varchar(255),
  member_id            		bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_member_address primary key (id))
;

create sequence member_address_seq;

alter table member_address add constraint fk_member_address_member foreign key (member_id) references member (id);
create index ix_member_address_member on member_address (member_id);

ALTER TABLE s_order ADD COLUMN consignee_name varchar(255);
ALTER TABLE s_order ADD COLUMN consignee_phone_number varchar(255);
ALTER TABLE s_order ADD COLUMN consigner_name varchar(255);
ALTER TABLE s_order ADD COLUMN consigner_phone_number varchar(255);
ALTER TABLE s_order ADD COLUMN origin_address varchar(255);
ALTER TABLE s_order ADD COLUMN destination_address varchar(255);
ALTER TABLE s_order ADD COLUMN order_id_shipper varchar(255);
ALTER TABLE s_order ADD COLUMN delivery_rates float;

# --- !Downs

drop table if exists member_address cascade;
drop sequence if exists member_address_seq;

ALTER TABLE s_order DROP COLUMN if exists consignee_name;
ALTER TABLE s_order DROP COLUMN if exists consignee_phone_number;
ALTER TABLE s_order DROP COLUMN if exists consigner_name;
ALTER TABLE s_order DROP COLUMN if exists consigner_phone_number;
ALTER TABLE s_order DROP COLUMN if exists origin_address;
ALTER TABLE s_order DROP COLUMN if exists destination_address;
ALTER TABLE s_order DROP COLUMN if exists order_id_shipper;
ALTER TABLE s_order DROP COLUMN if exists delivery_rates;
