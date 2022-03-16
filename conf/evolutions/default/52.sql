# --- !Ups

ALTER TABLE member
ALTER COLUMN gender TYPE varchar(15)

# --- !Downs
ALTER TABLE member
ALTER COLUMN gender TYPE varchar(15)