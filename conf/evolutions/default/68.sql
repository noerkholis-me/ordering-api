# --- !Ups

alter table feature add column is_merchant boolean;


# --- !Downs

alter table feature drop column if exists is_merchant;