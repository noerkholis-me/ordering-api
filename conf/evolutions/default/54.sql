# --- !Ups
create table webhook_status(
 id                        bigint not null,
 is_deleted                boolean,
 status                    boolean,
 created_at                timestamp not null,
 updated_at                timestamp not null,
 order_id		           varchar(7) not null,
 logistic		           varchar(30),
 constraint pk_webhook_status primary key (id))
;

create sequence webhook_status_seq;


# --- !Downs
drop table if exists webhook_status cascade;

drop sequence if exists webhook_status_seq;