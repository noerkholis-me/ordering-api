# --- !Ups
ALTER TABLE orders ADD column phone_number varchar(30);
ALTER TABLE orders ADD column member_name varchar(50);


# --- !Downs
ALTER TABLE orders DROP column if exists phone_number;
ALTER TABLE orders DROP column if exists member_name;