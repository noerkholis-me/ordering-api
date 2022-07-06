# --- !Ups

alter table member add column merchant_id bigint default null;
alter table member drop constraint if exists fk_member_merchant;
alter table member add constraint fk_member_merchant foreign key (merchant_id) references merchant (id);
create index idx_member_merchant on member (merchant_id);


# --- !Downs
alter table member drop column if exists merchant_id;