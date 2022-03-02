# --- !Ups
create table master_diamond_price (
  id                        bigint not null,
  is_deleted                boolean,
  user_id                    bigint not null,
  size_in_carat             varchar(255),
  clarity                   varchar(255),
  color                     varchar(255),
  price                     float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_master_diamond_price primary key (id))
;

ALTER TABLE master_diamond_price ADD CONSTRAINT master_diamond_price_fk_user_cms FOREIGN KEY (user_id) REFERENCES public."user_cms"(id);

create sequence master_diamond_price_seq;

# --- !Downs
drop table if exists master_diamond_price;
drop sequence if exists master_diamond_price_seq;