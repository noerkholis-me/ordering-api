# --- !Ups

create table pick_up_point_setup (
    id                         bigint,
    image_pupoint_setup        text,
    store_id                   bigint,
    merchant_id                bigint,
    is_deleted                 boolean,
    created_at                 timestamp not null,
    updated_at                 timestamp not null,
    constraint pk_pick_up_point_setup primary key (id)
);

create sequence pick_up_point_setup_seq;
alter table pick_up_point_setup drop constraint if exists fk_pick_up_point_setup_merchant_id_26;
alter table pick_up_point_setup add constraint fk_pick_up_point_setup_merchant_id_26 foreign key (merchant_id) references merchant (id);
create index idx_pick_up_point_setup_merchant_id_26 on pick_up_point_setup (merchant_id);

alter table pick_up_point_setup drop constraint if exists fk_pick_up_point_setup_store_id_27;
alter table pick_up_point_setup add constraint fk_pick_up_point_setup_store_id_27 foreign key (store_id) references store (id);
create index idx_pick_up_point_setup_store_id_27 on pick_up_point_setup (store_id);


# --- !Downs

drop table if exists pick_up_point_setup cascade;
drop sequence if exists pick_up_point_setup_seq;
