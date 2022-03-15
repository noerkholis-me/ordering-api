# --- !Ups

create table sales_order_status (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_id            bigint,
  status                    varchar(255),
  user_id            		bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sales_order_status primary key (id))
;

create sequence sales_order_status_seq;

alter table sales_order_status add constraint fk_sales_order_status_sales foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_status_sales_order on sales_order_status (sales_order_id);

alter table sales_order_status add constraint fk_sales_order_status_user_cms foreign key (user_id) references user_cms (id);
create index ix_sales_order_status_user_cms on sales_order_status (user_id);

# --- !Downs

drop table if exists sales_order_status cascade;
drop sequence if exists sales_order_status_seq;

