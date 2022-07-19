# --- !Ups
ALTER TABLE session_cashier
    ADD COLUMN end_total_amount_cash numeric,
    ADD COLUMN notes varchar;

# --- !Downs

ALTER TABLE session_cashier DROP COLUMN if exists end_total_amount_cash;
ALTER TABLE session_cashier DROP COLUMN if exists notes;