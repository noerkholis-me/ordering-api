# --- !Ups

create table fee_setting (
    id                          bigint,
    platform_fee                numeric,
    date                        timestamp,
    user_id                     bigint,
    is_deleted                  boolean,
    created_at                  timestamp not null,
    updated_at                  timestamp not null,
    constraint pk_fee_setting  primary key (id)
);

create sequence fee_setting_seq;
alter table fee_setting drop constraint if exists fk_fee_setting_user_id;
alter table fee_setting add constraint fk_fee_setting_user_id foreign key (user_id) references user_cms (id);
create index idx_fee_setting_user_id on fee_setting (user_id);

# --- !Downs

drop table if exists fee_setting cascade;
drop sequence if exists fee_setting_seq;