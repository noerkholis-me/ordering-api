# --- !Ups


ALTER TABLE banner_kios ADD COLUMN image_mobile_url varchar(255);
ALTER TABLE banner_kios ADD COLUMN image_mobile_size varchar(255);

# --- !Downs

ALTER TABLE banner_kios DROP COLUMN if exists image_mobile_url;
ALTER TABLE banner_kios DROP COLUMN if exists image_mobile_size;
