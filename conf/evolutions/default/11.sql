# --- !Ups

ALTER TABLE sales_order_detail ADD COLUMN loyalty_eligible_earn_referral int default 0;




# --- !Downs


ALTER TABLE sales_order_detail DROP COLUMN if exists loyalty_eligible_earn_referral; 