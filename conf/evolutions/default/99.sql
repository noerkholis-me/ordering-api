# --- !Ups

alter table banners add column banner_image_kiosk                text;
alter table pick_up_point_setup add column image_pup_landscape   text;


# --- !Downs

drop column if exists banner_image_kiosk;
drop column if exists image_pup_landscape;