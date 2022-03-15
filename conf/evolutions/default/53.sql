# --- !Ups
create table acc_user_management(
 id                        bigint not null,
 is_deleted                boolean,
 status                    boolean,
 created_at                timestamp not null,
 updated_at                timestamp not null,
 store_id		           bigint,
 user_cms_id		       bigint,
 constraint pk_acc_user_management primary key (id))
;

create sequence acc_user_management_seq;


# --- !Downs
drop table if exists acc_user_management cascade;

drop sequence if exists acc_user_management_seq;