# --- !Ups
alter table member drop constraint if exists uq_member_phone cascade;


# --- !Downs

