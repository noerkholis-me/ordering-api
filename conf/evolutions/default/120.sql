# --- !Ups
alter table orders add column total_loyalty_usage bigint default 0;

# --- !Downs
alter table orders drop column if exists total_loyalty_usage;