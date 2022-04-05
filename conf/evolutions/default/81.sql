# --- !Ups
ALTER TABLE images ADD COLUMN module varchar(255);
ALTER TABLE images ADD COLUMN image_key varchar(255);

# --- !Downs
alter table images drop column if exists module;
alter table images drop column if exists image_key;