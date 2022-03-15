# --- !Ups


ALTER TABLE faq add column content_api TEXT DEFAULT null;

# --- !Downs

ALTER TABLE faq DROP COLUMN if exists content_api;
