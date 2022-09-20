# --- !Ups
ALTER TABLE store ADD column store_logo text default null;


# --- !Downs
ALTER TABLE store DROP column if exists store_logo;