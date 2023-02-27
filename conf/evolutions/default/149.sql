# --- !Ups
alter table app_settings alter COLUMN mobile_qr_name type varchar(50);
alter table app_settings alter COLUMN kiosk_name type varchar(50);


# --- !Downs