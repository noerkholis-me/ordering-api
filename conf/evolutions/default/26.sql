# --- !Ups

ALTER TABLE promo ADD COLUMN notification_time timestamp;

# --- !Downs

ALTER TABLE promo DROP COLUMN if exists notification_time;