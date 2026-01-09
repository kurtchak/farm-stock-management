-- Customers table (pre evidenciu zákazníkov podľa telefónneho čísla)
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id BIGINT REFERENCES customers(id),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    total_quantity DECIMAL(10,2),
    unit_of_measure VARCHAR(10),
    notes TEXT,
    raw_sms_text TEXT,
    sender_phone VARCHAR(20) NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Order items table (jednotlivé položky objednávky)
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    stock_id BIGINT REFERENCES stocks(id),
    crop_id BIGINT REFERENCES crops(id),
    requested_product VARCHAR(100) NOT NULL,
    requested_quantity DECIMAL(10,2) NOT NULL,
    unit_of_measure VARCHAR(10),
    fulfilled_quantity DECIMAL(10,2) DEFAULT 0,
    available_quantity DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- SMS log table (pre debugging a audit)
CREATE TABLE sms_log (
    id BIGSERIAL PRIMARY KEY,
    sender_phone VARCHAR(20) NOT NULL,
    message_text TEXT NOT NULL,
    direction VARCHAR(10) NOT NULL DEFAULT 'IN',
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    order_id BIGINT REFERENCES orders(id),
    error_message TEXT,
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_customers_phone ON customers(phone_number);
CREATE INDEX idx_sms_log_sender ON sms_log(sender_phone);
CREATE INDEX idx_sms_log_direction ON sms_log(direction);