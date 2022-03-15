# --- !Ups

create table s_order_status (
  id                        bigint not null,
  is_deleted                boolean,
  s_order_id                bigint,
  status                    varchar(255),
  user_id            		bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_s_order_status primary key (id))
;

create sequence s_order_status_seq;

alter table s_order_status add constraint fk_s_order_status_s_order foreign key (s_order_id) references s_order (id);
create index ix_s_order_status_s_order on s_order_status (s_order_id);

alter table s_order_status add constraint fk_s_order_status_user_cms foreign key (user_id) references user_cms (id);
create index ix_s_order_status_user_cms on s_order_status (user_id);


# --- !Downs

drop table if exists s_order_status cascade;
drop sequence if exists s_order_status_seq;

