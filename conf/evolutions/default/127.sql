# --- !Ups
ALTER TABLE app_settings
    ADD COLUMN primary_color_kiosk              varchar(64) default null,
    ADD COLUMN secondary_color_kiosk            varchar(64) default null,
    ADD COLUMN app_logo_kiosk                   text default null,
    ADD COLUMN favicon_kiosk                    text default null;

# --- !Downs

ALTER TABLE app_settings DROP COLUMN if exists primary_color_kiosk;
ALTER TABLE app_settings DROP COLUMN if exists secondary_color_kiosk;
ALTER TABLE app_settings DROP COLUMN if exists app_logo_kiosk;
ALTER TABLE app_settings DROP COLUMN if exists favicon_kiosk;