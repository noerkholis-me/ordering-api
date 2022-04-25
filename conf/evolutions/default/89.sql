# --- !Ups

create table app_settings (
    id                         bigint,
    merchant_name              varchar(32),
    primary_color              varchar(64),
    secondary_color            varchar(64),
    app_logo                   text,
    favicon                    text,
    threshold                  integer,
    merchant_id                bigint,
    is_deleted                 boolean,
    created_at                 timestamp not null,
    updated_at                 timestamp not null,
    constraint pk_app_settings primary key (id)
);

create sequence app_settings_seq;
alter table app_settings drop constraint if exists fk_app_settings_merchant_id;
alter table app_settings add constraint fk_app_settings_merchant_id foreign key (merchant_id) references merchant (id);
create index idx_app_settings_merchant_id on app_settings (merchant_id);


# --- !Downs

drop table if exists app_settings cascade;
drop sequence if exists app_settings_seq;