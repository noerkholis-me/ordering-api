# --- !Ups

ALTER TABLE category_loyaltypoint ADD COLUMN cashback_type_referral int Default 1;
ALTER TABLE category_loyaltypoint ADD COLUMN cashback_value_referral float Default 0;
ALTER TABLE category_loyaltypoint ADD COLUMN max_cashback_value_referral float Default 0;



# --- !Downs


ALTER TABLE category_loyaltypoint DROP COLUMN if exists cashback_type_referral;
ALTER TABLE category_loyaltypoint DROP COLUMN if exists cashback_value_referral;
ALTER TABLE category_loyaltypoint DROP COLUMN if exists max_cashback_value_referral;