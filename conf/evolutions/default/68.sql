# --- !Ups

alter table feature add column if not exists is_merchant boolean;


# --- !Downs

alter table feature drop column if exists is_merchant;