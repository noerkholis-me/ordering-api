# --- !Ups
alter table member drop constraint if exists uq_member_email;

# --- !Downs