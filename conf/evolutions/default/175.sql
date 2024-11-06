# --- !Ups

-- Create a sequence for generating IDs
CREATE SEQUENCE order_detail_status_seq;

CREATE TABLE order_detail_status (
    id BIGINT NOT NULL DEFAULT nextval('order_detail_status_seq'), -- Use the sequence for ID generation
    order_detail_id BIGINT,
    code VARCHAR(255),
    name VARCHAR(255),
    description TEXT,
    is_active BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_order_detail_status PRIMARY KEY (id),
    FOREIGN KEY (order_detail_id) REFERENCES order_detail(id) ON DELETE CASCADE -- Uncommented and defined foreign key
);

# --- !Downs
DROP TABLE order_detail_status;
DROP SEQUENCE order_detail_status_seq; -- Drop the sequence in downs