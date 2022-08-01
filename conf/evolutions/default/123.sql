# --- !Ups
create table session_cashier (
       id                                           bigint not null,
       user_merchant_id                             bigint,
       store_id                                     bigint,
       session_code                                 varchar(50),
       start_total_amount                           numeric,
       end_total_amount                             numeric,
       start_time                                   timestamp not null,
       end_time                                     timestamp,
       is_active                                    boolean,
       is_deleted                                   boolean,
       created_at                                   timestamp not null,
       updated_at                                   timestamp not null,
       constraint pk_session_cashier                primary key (id)
);

create sequence session_cashier_seq;
alter table session_cashier drop constraint if exists fk_session_user_merchant_id;
alter table session_cashier add constraint fk_session_user_merchant_id foreign key (user_merchant_id) references user_merchant (id);
create index idx_user_merchant_session_cashier_id on session_cashier (user_merchant_id);

alter table session_cashier drop constraint if exists fk_session_store_id;
alter table session_cashier add constraint fk_session_store_id foreign key (store_id) references store (id);
create index idx_store_session_cashier_id on session_cashier (store_id);


# --- !Downs

drop table if exists session_cashier cascade;
drop sequence if exists session_cashier_seq;