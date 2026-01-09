-- Insert test users
INSERT INTO users (username, password, full_name, role, created_at) VALUES
    ('admin', '$2a$10$rYTLr1VXFfJboF9/97bE0.1zhkzWxvh/7JiHQQyr.WHc2GHx2xQFi', 'Admin User', 'ADMIN', NOW()),
    ('worker1', '$2a$10$zG1ZJyxV3wGq9JtyCrOqaOE7PFE9X8H5xHYuG2z.BzTVbKP8AQZP6', 'John Worker', 'WORKER', NOW());

-- Insert test crops
INSERT INTO crops (name, variety, harvest_season, description, storage_conditions, minimum_storage_temp, maximum_storage_temp, shelf_life_days, created_at) VALUES
    ('Potato', 'Russet', 'FALL', 'Standard potato variety', 'Cool and dark place', 7.0, 10.0, 180, NOW()),
    ('Carrot', 'Nantes', 'SUMMER', 'Sweet carrot variety', 'Cold storage', 0.0, 4.0, 30, NOW()),
    ('Apple', 'Honeycrisp', 'FALL', 'Sweet and crispy apples', 'Cold storage', 2.0, 4.0, 90, NOW());

-- Insert test stocks
INSERT INTO stocks (crop_id, batch_code, quantity, unit_of_measure, storage_location, harvest_date, quality_grade, notes, created_at, updated_at) VALUES
    (1, 'POT-2024-001', 1500.0, 'KG', 'Warehouse A, Section 1', '2024-01-15 10:00:00', 'A', 'First harvest of the year', NOW(), NOW()),
    (2, 'CAR-2024-001', 750.0, 'KG', 'Warehouse B, Section 2', '2024-01-20 11:00:00', 'A', 'Premium quality', NOW(), NOW()),
    (3, 'APP-2024-001', 1000.0, 'KG', 'Cold Storage 1', '2024-01-25 09:00:00', 'A', 'Excellent color', NOW(), NOW());

-- Insert test stock movements
INSERT INTO stock_movements (stock_id, user_id, movement_type, quantity, reason, created_at) VALUES
    (1, 1, 'IN', 1500.0, 'Initial stock', NOW()),
    (2, 1, 'IN', 750.0, 'Initial stock', NOW()),
    (3, 1, 'IN', 1000.0, 'Initial stock', NOW()),
    (1, 2, 'OUT', 100.0, 'Customer order #123', NOW()),
    (2, 2, 'OUT', 50.0, 'Customer order #124', NOW());