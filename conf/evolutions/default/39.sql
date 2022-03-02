# --- !Ups

ALTER TABLE s_order ADD COLUMN user_queue integer;


# --- !Downs

ALTER TABLE s_order DROP COLUMN if exists user_queue;
