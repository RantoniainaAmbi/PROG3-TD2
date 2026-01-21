DO $$ BEGIN
CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS dish_ingredient (
    id SERIAL PRIMARY KEY,
    id_dish INT REFERENCES dish(id),
    id_ingredient INT REFERENCES ingredient(id),
    quantity_required DOUBLE PRECISION,
    unit unit_type,
    CONSTRAINT fk_dish FOREIGN KEY(id_dish) REFERENCES dish(id),
    CONSTRAINT fk_ingredient FOREIGN KEY(id_ingredient) REFERENCES ingredient(id)
    );

ALTER TABLE ingredient DROP COLUMN IF EXISTS id_dish;

INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES
    (1, 1, 0.20, 'KG'),
    (1, 2, 0.15, 'KG'),
    (2, 3, 1.00, 'KG'),
    (4, 4, 0.30, 'KG'),
    (4, 5, 0.20, 'KG');

UPDATE dish SET price = 3500.00 WHERE id = 1;
UPDATE dish SET price = 12000.00 WHERE id = 2;
UPDATE dish SET price = NULL WHERE id = 3;
UPDATE dish SET price = 8000.00 WHERE id = 4;
UPDATE dish SET price = NULL WHERE id = 5;