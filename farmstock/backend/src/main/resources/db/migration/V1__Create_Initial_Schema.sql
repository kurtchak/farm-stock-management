-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Crops table
CREATE TABLE crops (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    variety VARCHAR(100) NOT NULL,
    harvest_season VARCHAR(20),
    description TEXT,
    storage_conditions TEXT,
    minimum_storage_temp REAL,
    maximum_storage_temp REAL,
    shelf_life_days INTEGER,
    created_at TIMESTAMP NOT NULL
);

-- Stocks table
CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    crop_id BIGINT NOT NULL REFERENCES crops(id),
    batch_code VARCHAR(20) NOT NULL UNIQUE,
    quantity DECIMAL(10,2) NOT NULL,
    unit_of_measure VARCHAR(10) NOT NULL,
    storage_location VARCHAR(100),
    harvest_date TIMESTAMP NOT NULL,
    quality_grade VARCHAR(10),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Stock movements table
CREATE TABLE stock_movements (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT NOT NULL REFERENCES stocks(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    movement_type VARCHAR(20) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    reason TEXT,
    created_at TIMESTAMP NOT NULL
);

-- Indexes
CREATE INDEX idx_stocks_crop_id ON stocks(crop_id);
CREATE INDEX idx_stock_movements_stock_id ON stock_movements(stock_id);
CREATE INDEX idx_stock_movements_user_id ON stock_movements(user_id);