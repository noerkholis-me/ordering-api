# --- !Ups
alter table role_merchant add column is_active boolean;

# --- !Downs
alter table role_merchant drop column if exists is_active;