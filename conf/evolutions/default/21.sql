# --- !Ups
ALTER TABLE diamond_type ADD COLUMN user_id bigint DEFAULT null;

alter table diamond_type add constraint diamond_type_fk_user_cms foreign key (user_id) references public."user_cms"(id);

# --- !Downs
alter table diamond_type drop constraint if exists diamond_type_fk_user_cms;

ALTER TABLE diamond_type DROP COLUMN if exists user_id;