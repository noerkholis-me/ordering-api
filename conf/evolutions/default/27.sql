# --- !Ups
create table setting_exchange_rate_custom_diamond (
  id                        bigint not null,
  is_deleted                boolean,
  date			    timestamp,
  idr_rate		    float,
  user_id		    bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_setting_exchange_rate_custom_diamond primary key (id))
;

ALTER TABLE setting_exchange_rate_custom_diamond ADD CONSTRAINT setting_exchange_rate_custom_diamond_fk_user_cms FOREIGN KEY (user_id) REFERENCES public."user_cms"(id);

create sequence setting_exchange_rate_custom_diamond_seq;


# --- !Downs
drop table if exists setting_exchange_rate_custom_diamond;

drop sequence if exists setting_exchange_rate_custom_diamond_seq;
