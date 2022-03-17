# --- !Ups


create table store (
 id                        bigint not null,
 is_deleted                boolean,
 status                    boolean,
 store_code                varchar(4) not null,
 store_name                varchar(50) not null,
 store_address             TEXT not null,
 store_phone               varchar(20) not null,
 created_at                timestamp not null,
 updated_at                timestamp not null,
 user_id                   bigint,
 province_id				  bigint,
 shipper_city_id			  bigint,
 suburb_id				  bigint,
 area_id					  bigint,
 store_gmap				  TEXT,
 store_long				  double precision,
 store_lat				  double precision,
 constraint pk_store primary key (id))
;

create sequence store_seq;

# --- !Downs
drop table if exists store cascade;
drop sequence if exists store_seq;
