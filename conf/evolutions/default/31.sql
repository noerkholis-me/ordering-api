# --- !Ups


ALTER TABLE attribute_group ADD COLUMN is_deleted boolean;
ALTER TABLE attribute_group ADD COLUMN status boolean;
ALTER TABLE attribute_group ADD COLUMN description varchar(255);
ALTER TABLE attrib ADD COLUMN status boolean;
ALTER TABLE attrib ADD COLUMN is_deleted boolean;


# --- !Downs

ALTER TABLE attribute_group DROP COLUMN if exists is_deleted;
ALTER TABLE attribute_group DROP COLUMN if exists status;
ALTER TABLE attribute_group DROP COLUMN if exists description;
ALTER TABLE attrib DROP COLUMN if exists status;
ALTER TABLE attrib DROP COLUMN if exists is_deleted;

