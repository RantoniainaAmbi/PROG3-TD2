CREATE DATABASE mini_dish_db;
CREATE USER mini_dish_db_manager WITH PASSWORD '123456';
GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_db_manager;
GRANT CREATE ON SCHEMA public TO mini_dish_db_manager;
GRANT USAGE ON SCHEMA public TO mini_dish_db_manager;

                      DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='dish' AND column_name='price') THEN
ALTER TABLE dish ADD COLUMN price DOUBLE PRECISION;
END IF;
END $$;

UPDATE dish SET price = 2000 WHERE name = 'Salade fraîche';
UPDATE dish SET price = 6000 WHERE name = 'Poulet grillé';