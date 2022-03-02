# --- !Ups


ALTER TABLE cart ADD COLUMN note varchar(255);


# --- !Downs

ALTER TABLE cart DROP COLUMN if exists note;
