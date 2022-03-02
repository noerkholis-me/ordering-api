# --- !Ups

ALTER TABLE wish_list ADD COLUMN stock_history boolean;

ALTER TABLE wish_list ADD COLUMN price_history float;

# --- !Downs

ALTER TABLE wish_list DROP COLUMN if exists stock_history;

ALTER TABLE wish_list DROP COLUMN if exists price_history;