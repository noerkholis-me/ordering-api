# --- !Ups
ALTER TABLE brand ALTER COLUMN odoo_id TYPE  varchar ;

ALTER TABLE category ALTER COLUMN odoo_id TYPE varchar ;

ALTER TABLE product ALTER COLUMN odoo_id TYPE varchar ;

ALTER TABLE product_tmp ALTER COLUMN odoo_id TYPE varchar ;


# --- !Downs
ALTER TABLE brand ALTER COLUMN odoo_id TYPE int USING odoo_id::integer;

ALTER TABLE category ALTER COLUMN odoo_id TYPE int USING odoo_id::integer;

ALTER TABLE product ALTER COLUMN odoo_id TYPE int USING odoo_id::integer;

ALTER TABLE product_tmp ALTER COLUMN odoo_id TYPE int USING odoo_id::integer;