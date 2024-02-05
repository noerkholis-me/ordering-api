# --- !Ups

create table banner_bazaar(
    id                        bigint not null,
    is_deleted                boolean,
    title                     varchar(255),
    image_url                 varchar(255),
    url                       varchar(255),
    created_at                timestamp not null,
    updated_at                timestamp not null,
    constraint pk_banner_bazaar primary key (id)
);

create sequence banner_bazaar_seq;


# --- !Downs
drop sequence if exists banner_bazaar_seq;