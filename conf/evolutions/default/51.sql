# --- !Ups
create table access_store (
    id                     bigint not null,
    is_deleted             boolean,
    store_id             bigint,
    user_id                bigint,
    created_at             timestamp not null,
    updated_at             timestamp not null,
    constraint pk_access_store primary key(id)
);

create sequence access_store_seq;

alter table access_store add constraint fk_store_id foreign key (store_id) references store(id);

alter table access_store add constraint fk_user_id foreign key (user_id) references user_cms(id);



    
# --- !Downs
drop table if exists access_store cascade;

drop sequence if exists access_store_seq;
