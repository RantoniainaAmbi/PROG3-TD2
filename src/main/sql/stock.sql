DO $$ BEGIN
CREATE TYPE movement_type AS ENUM ('IN', 'OUT');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS stock_movement (
    id SERIAL PRIMARY KEY,
    id_ingredient INT REFERENCES ingredient(id),
    quantity DOUBLE PRECISION NOT NULL,
    type movement_type NOT NULL,
    unit unit_type DEFAULT 'KG',
    creation_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

INSERT INTO stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime) VALUES
    (1, 1, 5.0, 'IN', 'KG', '2024-01-05 08:00:00'),
    (2, 1, 0.2, 'OUT', 'KG', '2024-01-06 12:00:00'),
    (3, 2, 4.0, 'IN', 'KG', '2024-01-05 08:00:00'),
    (4, 2, 0.15, 'OUT', 'KG', '2024-01-06 12:00:00'),
    (5, 3, 10.0, 'IN', 'KG', '2024-01-04 09:00:00'),
    (6, 3, 1.0, 'OUT', 'KG', '2024-01-06 13:00:00'),
    (7, 4, 3.0, 'IN', 'KG', '2024-01-05 10:00:00'),
    (8, 4, 0.3, 'OUT', 'KG', '2024-01-06 14:00:00'),
    (9, 5, 2.5, 'IN', 'KG', '2024-01-05 10:00:00'),
    (10, 5, 0.2, 'OUT', 'KG', '2024-01-06 14:00:00')
    ON CONFLICT (id) DO NOTHING;