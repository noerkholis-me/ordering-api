# --- !Ups

alter table orders add column long_lat_destination VARCHAR(255) default null;


# --- !Downs

alter table orders drop column if exists long_lat_destination;