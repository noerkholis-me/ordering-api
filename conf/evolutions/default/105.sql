# --- !Ups

create table loyalty_point_merchant (
      id                                bigint,
      usage_type                        varchar(50),
      loyalty_usage_value               numeric,
      max_loyalty_usage_value           numeric,
      cashback_type                     varchar(50),
      cashback_value                    numeric,
      max_cashback_value                numeric,
      merchant_id                       bigint,
      is_deleted                        boolean,
      created_at                        timestamp not null,
      updated_at                        timestamp not null,
      constraint pk_loyalty_point_merchant primary key (id)
);

create sequence loyalty_point_merchant_seq;
alter table loyalty_point_merchant drop constraint if exists fk_loyalty_point_merchant_id;
alter table loyalty_point_merchant add constraint fk_loyalty_point_merchant_id foreign key (merchant_id) references merchant (id);
create index idx_loyalty_point_merchant_merchant_id on loyalty_point_merchant (merchant_id);


# --- !Downs

drop table if exists loyalty_point_merchant cascade;
drop sequence if exists loyalty_point_merchant_seq;