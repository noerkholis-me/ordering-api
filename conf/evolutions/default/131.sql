# --- !Ups
ALTER TABLE app_settings ADD column mobile_qr_name varchar(20);
ALTER TABLE app_settings ADD column kiosk_name varchar(20);


# --- !Downs
ALTER TABLE app_settings DROP column if exists mobile_qr_name;
ALTER TABLE app_settings DROP column if exists kiosk_name;