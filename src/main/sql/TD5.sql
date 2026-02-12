SELECT unit,
       SUM(CASE WHEN type = 'OUT' THEN -quantity ELSE quantity END) as actual_quantity
FROM stock_movement
WHERE id_ingredient = ? AND creation_datetime <= ?
GROUP BY unit;

SELECT SUM(di.quantity_required * i.price) AS total_cost
FROM dish_ingredient di
         JOIN ingredient i ON di.id_ingredient = i.id
WHERE di.id_dish = ?;


SELECT
    d.price - (
        SELECT SUM(di.quantity_required * i.price)
        FROM dish_ingredient di
                 JOIN ingredient i ON di.id_ingredient = i.id
        WHERE di.id_dish = d.id
    ) AS gross_margin
FROM dish d
WHERE d.id = ?;

SELECT
    date_trunc(?, creation_datetime) AS period,
    SUM(CASE WHEN type = 'OUT' THEN -quantity ELSE quantity END) AS delta_quantity
FROM stock_movement
WHERE id_ingredient = ?
  AND creation_datetime BETWEEN ? AND ?
GROUP BY period
ORDER BY period;