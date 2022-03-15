alter table merchant add column role_id int;
alter table merchant add constraint fk_merchant_role foreign key (role_id) references role (id);
create index ix_merchant_role on merchant (role_id);