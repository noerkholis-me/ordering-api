# --- !Ups

ALTER TABLE wish_list ADD COLUMN notification_count integer;

# --- !Downs

ALTER TABLE wish_list DROP COLUMN if exists notification_count;