# --- !Ups

create table shipper_order_status (
  id                        bigint not null,
  is_deleted                boolean,
  order_id                  bigint,
  status                    varchar(255),
  notes                     varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_shipper_order_status primary key (id))
;

create sequence shipper_order_status_seq;

alter table shipper_order_status add constraint fk_s_shipper_order_status_orders foreign key (order_id) references orders (id);
create index ix_s_shipper_order_status_orders on shipper_order_status (order_id);


# --- !Downs

drop table if exists shipper_order_status cascade;
drop sequence if exists shipper_order_status_seq;

