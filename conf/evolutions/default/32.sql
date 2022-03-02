# --- !Ups


create table banner_kios (
  id                        bigint not null,
  is_deleted                boolean,
  banner_name               varchar(255),
  slug               		varchar(255),
  status                    boolean,
  sequence                  integer,
  position_id               integer,
  open_new_tab              boolean,
  link_url                  varchar(255),
  image_name         		TEXT,
  image_promo_page_url      varchar(255),
  image_promo_page_size     varchar(255),
  image_home_page_url      	varchar(255),
  image_home_page_size    	varchar(255),
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_banner_kios primary key (id))
;

create sequence banner_kios_seq;

alter table banner_kios add constraint fk_banner_kios_user_cms foreign key (user_id) references user_cms (id);
create index ix_banner_kios_user_cms on banner_kios (user_id);

# --- !Downs

drop table if exists banner_kios cascade;
drop sequence if exists banner_kios_seq;





