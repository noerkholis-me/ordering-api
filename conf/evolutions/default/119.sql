# --- !Ups
alter table member add column loyalty_point bigint default 0;

# --- !Downs
alter table member drop column if exists loyalty_point;