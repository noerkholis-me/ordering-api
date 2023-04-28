# --- !Ups

alter table store add column status_open_store bool;
alter table store add column open_at  varchar (30) ;
alter table store add column closed_at  varchar (30) ;

# --- !Downs

drop column if exists status_open_store;
drop column if exists open_at;
drop column if exists closed_at;