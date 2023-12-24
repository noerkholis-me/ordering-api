# --- !Ups
CREATE TABLE store_ratings(
                               id						bigint not null,
                               store_id					bigint not null,
                               member_id			    bigint not null,
                               feedback                 text,
                               rate                     float,
                               is_deleted				boolean,
                               created_at                timestamp not null,
                               updated_at                timestamp not null,
                               constraint pk_store_ratings primary key (id));

ALTER TABLE store_ratings add constraint fk_store_ratings_store_id foreign key (store_id) references store(id);
ALTER TABLE store_ratings add constraint fk_store_ratings_member_id foreign key (member_id) references member(id);

CREATE SEQUENCE store_ratings_seq
    INCREMENT 1
START 1;

CREATE TABLE product_ratings(
                              id						bigint not null,
                              store_id					bigint not null,
                              member_id			        bigint not null,
                              order_number              varchar(255),
                              product_merchant_id       bigint not null,
                              feedback                  text,
                              rate                      float,
                              is_deleted				boolean,
                              created_at                timestamp not null,
                              updated_at                timestamp not null,
                              constraint pk_product_ratings primary key (id));

ALTER TABLE product_ratings add constraint fk_product_ratings_store_id foreign key (store_id) references store(id);
ALTER TABLE product_ratings add constraint fk_product_ratings_member_id foreign key (member_id) references member(id);
ALTER TABLE product_ratings add constraint fk_product_ratings_product_merchant_id foreign key (product_merchant_id) references product_merchant(id);

CREATE SEQUENCE product_ratings_seq
    INCREMENT 1
START 1;

# --- !Downs

DROP SEQUENCE IF EXISTS  store_ratings_seq;
drop table if exists store_ratings;
