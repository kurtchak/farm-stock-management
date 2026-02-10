-- Forest items (materials: trees, stakes, protection, etc.)
CREATE TABLE forest_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL CHECK (category IN ('TREE', 'STAKE', 'PROTECTION', 'OTHER')),
    quantity DECIMAL(10,2) NOT NULL DEFAULT 0,
    unit VARCHAR(10) NOT NULL DEFAULT 'ks',
    min_stock DECIMAL(10,2) NOT NULL DEFAULT 0,
    notes TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Planting set definitions
CREATE TABLE planting_sets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    color VARCHAR(20) NOT NULL DEFAULT '#16a34a',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Planting set components (which items and how many per execution)
CREATE TABLE planting_set_items (
    id BIGSERIAL PRIMARY KEY,
    planting_set_id BIGINT NOT NULL REFERENCES planting_sets(id) ON DELETE CASCADE,
    forest_item_id BIGINT NOT NULL REFERENCES forest_items(id),
    quantity DECIMAL(10,2) NOT NULL
);

-- Forest movement audit trail
CREATE TABLE forest_movements (
    id BIGSERIAL PRIMARY KEY,
    forest_item_id BIGINT NOT NULL REFERENCES forest_items(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    movement_type VARCHAR(10) NOT NULL CHECK (movement_type IN ('IN', 'OUT')),
    quantity DECIMAL(10,2) NOT NULL,
    reason TEXT,
    planting_set_id BIGINT REFERENCES planting_sets(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_forest_items_category ON forest_items(category);
CREATE INDEX idx_forest_items_deleted ON forest_items(deleted);
CREATE INDEX idx_planting_set_items_set_id ON planting_set_items(planting_set_id);
CREATE INDEX idx_planting_set_items_item_id ON planting_set_items(forest_item_id);
CREATE INDEX idx_forest_movements_item_id ON forest_movements(forest_item_id);
CREATE INDEX idx_forest_movements_user_id ON forest_movements(user_id);
CREATE INDEX idx_forest_movements_created_at ON forest_movements(created_at);
