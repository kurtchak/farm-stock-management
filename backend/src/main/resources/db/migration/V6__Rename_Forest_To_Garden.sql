-- Rename forest_items -> garden_items
ALTER TABLE forest_items RENAME TO garden_items;

-- Rename forest_movements -> garden_movements
ALTER TABLE forest_movements RENAME TO garden_movements;

-- Rename FK column in planting_set_items
ALTER TABLE planting_set_items RENAME COLUMN forest_item_id TO garden_item_id;

-- Rename FK column in garden_movements
ALTER TABLE garden_movements RENAME COLUMN forest_item_id TO garden_item_id;

-- Drop old indexes
DROP INDEX IF EXISTS idx_forest_items_category;
DROP INDEX IF EXISTS idx_forest_items_deleted;
DROP INDEX IF EXISTS idx_planting_set_items_item_id;
DROP INDEX IF EXISTS idx_forest_movements_item_id;
DROP INDEX IF EXISTS idx_forest_movements_user_id;
DROP INDEX IF EXISTS idx_forest_movements_created_at;

-- Create new indexes with garden naming
CREATE INDEX idx_garden_items_category ON garden_items(category);
CREATE INDEX idx_garden_items_deleted ON garden_items(deleted);
CREATE INDEX idx_planting_set_items_item_id ON planting_set_items(garden_item_id);
CREATE INDEX idx_garden_movements_item_id ON garden_movements(garden_item_id);
CREATE INDEX idx_garden_movements_user_id ON garden_movements(user_id);
CREATE INDEX idx_garden_movements_created_at ON garden_movements(created_at);
