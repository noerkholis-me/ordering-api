# --- !Ups
create table loyalty_point_history (
        id                                  bigint not null,
        member_id                           bigint,
        order_id                            bigint,
        point                               bigint,
        added                               bigint,
        used                                bigint,
        merchant_id                         bigint,
        expired                             timestamp not null,
        is_deleted                          boolean,
        created_at                          timestamp not null,
        updated_at                          timestamp not null,
        constraint pk_loyalty_point_history primary key (id)
);

create sequence loyalty_point_history_seq;
alter table loyalty_point_history drop constraint if exists fk_loyalty_point_history_merchant_id;
alter table loyalty_point_history add constraint fk_loyalty_point_history_merchant_id foreign key (merchant_id) references merchant (id);
create index idx_loyalty_point_history_merchant_id on loyalty_point_history (merchant_id);

alter table loyalty_point_history drop constraint if exists fk_loyalty_point_history_member_id;
alter table loyalty_point_history add constraint fk_loyalty_point_history_member_id foreign key (member_id) references member (id);
create index idx_loyalty_point_history_member_id on loyalty_point_history (member_id);

alter table loyalty_point_history drop constraint if exists fk_loyalty_point_history_order_id;
alter table loyalty_point_history add constraint fk_loyalty_point_history_order_id foreign key (order_id) references orders (id);
create index idx_loyalty_point_history_order_id on loyalty_point_history (order_id);


# --- !Downs

drop table if exists loyalty_point_history cascade;
drop sequence if exists loyalty_point_history_seq;