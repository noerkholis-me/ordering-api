# --- !Ups


ALTER TABLE s_order add column tracking_url TEXT DEFAULT null;

# --- !Downs

ALTER TABLE s_order DROP COLUMN if exists tracking_url;
