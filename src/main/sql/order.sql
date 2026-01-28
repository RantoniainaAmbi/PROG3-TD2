CREATE SEQUENCE IF NOT EXISTS order_ref_seq START 1;

CREATE TABLE "order" (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(8) UNIQUE NOT NULL,
    creation_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dish_order (
    id SERIAL PRIMARY KEY,
    id_order INT REFERENCES "order"(id),
    id_dish INT REFERENCES dish(id),
    quantity INT NOT NULL
);