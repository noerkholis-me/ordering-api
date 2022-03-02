# --- !Ups

create table setting_tax_service (
  id                        bigint not null,
  is_deleted                boolean,
  tax                		float,
  service        			float,
  date      				timestamp,
  user_id            		bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_setting_tax_service primary key (id))
;

create sequence setting_tax_service_seq;

alter table setting_tax_service add constraint fk_setting_tax_service_user_cms foreign key (user_id) references user_cms (id);
create index ix_setting_tax_service_user_cms on setting_tax_service (user_id);

# --- !Downs

drop table if exists setting_tax_service cascade;
drop sequence if exists setting_tax_service_seq;

