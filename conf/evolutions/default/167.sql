# --- !Ups

create table banner_bazar(
    id                        bigint not null,
    is_deleted                boolean,
    title                     varchar(255),
    image_url                 varchar(255),
    url                       varchar(255),
    created_at                timestamp not null,
    updated_at                timestamp not null,
    constraint pk_banner_bazar primary key (id)
);

create sequence banner_bazar_seq;


# --- !Downs
drop sequence if exists banner_bazar_seq;