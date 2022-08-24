# --- !Ups
ALTER TABLE table_merchant ADD column is_available boolean default true;


# --- !Downs
ALTER TABLE table_merchant DROP column if exists is_available;