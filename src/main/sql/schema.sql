CREATE TYPE category AS ENUM ('VEGETABLE','ANIMAL','MARINE','DAIRY','OTHER');
CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT')

CREATE TABLE ingredient(
    id int primary key ,
    name varchar,
    price numeric,
    category category,
    id_dish int REFERENCES dish(id)
);

CREATE TABLE dish(
    id int primary key ,
    name varchar,
    dish_type dish_type
);