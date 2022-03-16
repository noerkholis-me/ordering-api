# --- !Ups


create table s_order_backend_status (
 id                        bigint not null,
 is_deleted                boolean,
 code                      varchar(7),
 created_at                timestamp not null,
 updated_at                timestamp not null,
 constraint pk_s_order_data_status primary key (id))
;

create sequence s_order_backend_status_seq;

# --- !Downs
drop table if exists s_order_backend_status cascade;
drop sequence if exists s_order_backend_status_seq;
