# --- !Ups

create table role_merchant_feature (
      feature_id                bigint,
      role_merchant_id          bigint,
      is_view                    boolean,
      is_add                     boolean,
      is_edit                    boolean,
      is_delete                  boolean)
;

alter table role_merchant_feature drop constraint if exists fk_role_merchant_feature_feature_141;
alter table role_merchant_feature add constraint fk_role_merchant_feature_feature_141 foreign key (feature_id) references feature (id);
create index ix_role_merchant_feature_141 on role_merchant_feature (feature_id);

alter table role_merchant_feature drop constraint if exists fk_role_merchant_feature_role_142;
alter table role_merchant_feature add constraint fk_role_merchant_feature_role_142 foreign key (role_merchant_id) references role_merchant (id);
create index ix_role_merchant_feature_role_142 on role_merchant_feature (role_merchant_id);

# --- !Downs

drop table if exists role_merchant_feature cascade;