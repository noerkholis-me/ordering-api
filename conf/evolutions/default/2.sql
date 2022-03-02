# --- !Ups
create table banner_megamenu (
  id                        bigint not null,
  title						varchar(255),
  url						varchar(255),
  image_url					varchar(255),
  constraint pk_banner_megamenu primary key (id))
;

# --- !Downs
drop table if exists banner_megamenu cascade;