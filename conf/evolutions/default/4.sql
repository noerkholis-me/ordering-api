# --- !Ups
create table loyaltypoint (
  id                        bigint not null,
  is_deleted                boolean,
  member_id					bigint not null,
  transaction_id			bigint,
  point						bigint,
  used						bigint,
  expired_date				timestamp,
  note						varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_loyaltypoint primary key (id))
;

ALTER TABLE loyaltypoint ADD CONSTRAINT loyaltypoint_fk_member FOREIGN KEY (member_id) REFERENCES public."member"(id);
ALTER TABLE loyaltypoint ADD CONSTRAINT loyaltypoint_fk_salesorder FOREIGN KEY (transaction_id) REFERENCES public.sales_order(id);


# --- !Downs
drop table if exists loyaltypoint;
