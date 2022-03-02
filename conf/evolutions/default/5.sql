# --- !Ups

ALTER TABLE member ADD COLUMN apple_user_id varchar(255) DEFAULT null;

alter table member add constraint uq_member_apple_user_id unique (apple_user_id);


# --- !Downs

alter table member drop constraint if exists uq_member_apple_user_id;

ALTER TABLE member DROP COLUMN if exists apple_user_id;