# --- !Ups

alter table merchant add column merchant_service_type  varchar (100) ;

# --- !Downs

drop column if exists merchant_service_type;