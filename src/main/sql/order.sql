CREATE TABLE order(
    id serial primary key ,
    reference varchar,
    creation_datetime timestamp
);

CREATE TABLE dish_order(
    id serial primary key ,
    id_order int REFERENCES order(id),
    id_dish int REFERENCES dish(id),
    quantity int
);

