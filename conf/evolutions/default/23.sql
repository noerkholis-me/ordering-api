# --- !Ups
ALTER TABLE master_size_in_carat_custom_diamond ADD COLUMN user_id bigint DEFAULT null;

alter table master_size_in_carat_custom_diamond add constraint master_size_in_carat_custom_diamond_fk_user_cms foreign key (user_id) references public."user_cms"(id);

ALTER TABLE master_color_custom_diamond ADD COLUMN user_id bigint DEFAULT null;

alter table master_color_custom_diamond add constraint master_color_custom_diamond_fk_user_cms foreign key (user_id) references public."user_cms"(id);

ALTER TABLE master_clarity_custom_diamond ADD COLUMN user_id bigint DEFAULT null;

alter table master_clarity_custom_diamond add constraint master_clarity_custom_diamond_fk_user_cms foreign key (user_id) references public."user_cms"(id);

# --- !Downs
alter table master_size_in_carat_custom_diamond drop constraint if exists master_size_in_carat_custom_diamond_fk_user_cms;

ALTER TABLE master_size_in_carat_custom_diamond DROP COLUMN if exists user_id;

alter table master_color_custom_diamond drop constraint if exists master_color_custom_diamond_fk_user_cms;

ALTER TABLE master_color_custom_diamond DROP COLUMN if exists user_id;

alter table master_clarity_custom_diamond drop constraint if exists master_clarity_custom_diamond_fk_user_cms;

ALTER TABLE master_clarity_custom_diamond DROP COLUMN if exists user_id;