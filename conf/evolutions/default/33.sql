# --- !Ups


ALTER TABLE brand ADD COLUMN description varchar(255);

# --- !Downs

ALTER TABLE brand DROP COLUMN if exists description;
