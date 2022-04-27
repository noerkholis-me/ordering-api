# --- !Ups

create table pick_up_point (
    id                         bigint,
    pupoint_name               varchar(20),
    store_id                   bigint,
    merchant_id                bigint,
    is_active                  boolean,
    is_deleted                 boolean,
    created_at                 timestamp not null,
    updated_at                 timestamp not null,
    constraint pk_pick_up_point primary key (id)
);

create sequence pick_up_point_seq;
alter table pick_up_point drop constraint if exists fk_pick_up_point_merchant_id_26;
alter table pick_up_point add constraint fk_pick_up_point_merchant_id_26 foreign key (merchant_id) references merchant (id);
create index idx_pick_up_point_merchant_id_26 on pick_up_point (merchant_id);

alter table pick_up_point drop constraint if exists fk_pick_up_point_store_id_27;
alter table pick_up_point add constraint fk_pick_up_point_store_id_27 foreign key (store_id) references store (id);
create index idx_pick_up_point_store_id_27 on pick_up_point (store_id);


# --- !Downs

drop table if exists pick_up_point cascade;
drop sequence if exists pick_up_point_seq;
