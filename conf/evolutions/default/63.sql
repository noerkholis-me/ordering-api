# --- !Ups


ALTER TABLE s_order add column postpone_url_midtrans varchar(255) default null;

# --- !Downs

ALTER TABLE s_order DROP COLUMN if exists postpone_url_midtrans;
