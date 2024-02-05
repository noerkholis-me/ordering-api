# --- !Ups
CREATE TABLE store_taggings(
                               id						bigint not null,
                               store_id					bigint not null,
                               name					    varchar(100) not null,
                               status					boolean,
                               is_deleted				boolean,
                               created_at                timestamp not null,
                               updated_at                timestamp not null,
                               constraint pk_store_taggings primary key (id));

ALTER TABLE store_taggings add constraint fk_store_taggings_store_id foreign key (store_id) references store(id);

alter table store add column store_qr_code_static text;



CREATE SEQUENCE store_taggings_seq
    INCREMENT 1
START 1;

# --- !Downs

DROP SEQUENCE IF EXISTS  store_taggings_seq;
drop table if exists store_taggings;

alter table store drop column if exists store_qr_code_static;
