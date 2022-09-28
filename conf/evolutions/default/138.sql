# --- !Ups

create table shipper_order_status (
  id                        bigint not null,
  is_deleted                boolean,
  order_id                  bigint,
  status                    varchar(255),
  user_id            		bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_shipper_order_status primary key (id))
;

create sequence shipper_order_status_seq;

alter table shipper_order_status add constraint fk_shipper_order_status_orders foreign key (order_id) references orders (id);
create index ix_shipper_order_status_orders on shipper_order_status (order_id);

alter table shipper_order_status add constraint fk_shipper_order_status_user_cms foreign key (user_id) references user_cms (id);
create index ix_shipper_order_status_user_cms on s_order_status (user_id);


# --- !Downs

drop table if exists shipper_order_status cascade;
drop sequence if exists shipper_order_status_seq;

