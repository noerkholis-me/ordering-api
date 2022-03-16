# --- !Ups


ALTER TABLE page add column content_api TEXT DEFAULT null;

# --- !Downs

ALTER TABLE page DROP COLUMN if exists content_api;
