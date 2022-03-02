# --- !Ups


ALTER TABLE member ADD COLUMN token varchar(255);
ALTER TABLE member ADD COLUMN otp varchar(6);

ALTER TABLE member ADD COLUMN token_expire_time timestamp;
ALTER TABLE member ADD COLUMN otp_expire_time timestamp;


# --- !Downs

ALTER TABLE member DROP COLUMN if exists token;
ALTER TABLE member DROP COLUMN if exists otp;
ALTER TABLE member DROP COLUMN if exists token_expire_time;
ALTER TABLE member DROP COLUMN if exists otp_expire_time;
