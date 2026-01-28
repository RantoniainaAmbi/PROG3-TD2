package com.java;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRetriever {

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public Dish findDishById(Integer id) {
        Connection connection = null;
        try {
            connection = new DBConnection().getConnection();
            String sql = "SELECT id, name, dish_type, price FROM dish WHERE id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Dish dish = new Dish();
                        dish.setId(rs.getInt("id"));
                        dish.setName(rs.getString("name"));
                        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                        dish.setPrice(rs.getObject("price") == null ? null : rs.getDouble("price"));

                        dish.setDishIngredients(findDishIngredientsByDishId(id));
                        return dish;
                    }
                }
            }
            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    private List<DishIngredient> findDishIngredientsByDishId(Integer idDish) {
        Connection connection = null;
        List<DishIngredient> results = new ArrayList<>();
        String sql = """
                SELECT i.id, i.name, i.price, i.category, di.quantity_required, di.unit
                FROM dish_ingredient di
                JOIN ingredient i ON di.id_ingredient = i.id
                WHERE di.id_dish = ?
                """;
        try {
            connection = new DBConnection().getConnection();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idDish);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Ingredient ing = new Ingredient();
                        ing.setId(rs.getInt("id"));
                        ing.setName(rs.getString("name"));
                        ing.setPrice(rs.getDouble("price"));
                        ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                        ing.setStockMovementList(findStockMovementsByIngredientId(ing.getId()));

                        DishIngredient di = new DishIngredient();
                        di.setIngredient(ing);
                        di.setQuantityRequired(rs.getDouble("quantity_required"));
                        di.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                        results.add(di);
                    }
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public Dish saveDish(Dish toSave) {
        Connection conn = null;
        String upsertDishSql = """
                INSERT INTO dish (id, price, name, dish_type)
                VALUES (?, ?, ?, ?::dish_type)
                ON CONFLICT (id) DO UPDATE
                SET name = EXCLUDED.name,
                    dish_type = EXCLUDED.dish_type,
                    price = EXCLUDED.price
                RETURNING id
                """;

        try {
            conn = new DBConnection().getConnection();
            conn.setAutoCommit(false);
            Integer dishId;

            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }

                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            List<DishIngredient> currentIngredients = toSave.getDishIngredients();
            detachIngredients(conn, dishId, currentIngredients);
            attachIngredients(conn, dishId, currentIngredients);

            conn.commit();

            closeConnection(conn);
            conn = null;

            return findDishById(dishId);
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private void detachIngredients(Connection conn, Integer dishId, List<DishIngredient> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dish_ingredient WHERE id_dish = ?")) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            }
            return;
        }
        String inClause = items.stream()
                .map(di -> String.valueOf(di.getIngredient().getId()))
                .collect(Collectors.joining(","));
        String sql = String.format("DELETE FROM dish_ingredient WHERE id_dish = ? AND id_ingredient NOT IN (%s)", inClause);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void attachIngredients(Connection conn, Integer dishId, List<DishIngredient> items) throws SQLException {
        if (items == null || items.isEmpty()) return;
        String sql = """
                INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?::unit_type)
                ON CONFLICT (id_dish, id_ingredient) DO UPDATE 
                SET quantity_required = EXCLUDED.quantity_required, unit = EXCLUDED.unit
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : items) {
                ps.setInt(1, dishId);
                ps.setInt(2, di.getIngredient().getId());
                ps.setDouble(3, di.getQuantityRequired());
                ps.setString(4, di.getUnit().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    public Ingredient findIngredientById(Integer id) {
        Connection connection = null;
        try {
            connection = new DBConnection().getConnection();
            String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setId(rs.getInt("id"));
                        ingredient.setName(rs.getString("name"));
                        ingredient.setPrice(rs.getDouble("price"));
                        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                        ingredient.setStockMovementList(findStockMovementsByIngredientId(id));
                        return ingredient;
                    }
                }
            }
            throw new RuntimeException("Ingredient not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    private List<StockMovement> findStockMovementsByIngredientId(Integer ingredientId) {
        Connection connection = null;
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT id, quantity, type, unit, creation_datetime FROM stock_movement WHERE id_ingredient = ?";

        try {
            connection = new DBConnection().getConnection();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, ingredientId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        StockMovement sm = new StockMovement();
                        sm.setId(rs.getInt("id"));
                        sm.setType(MovementTypeEnum.valueOf(rs.getString("type")));
                        sm.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                        sm.setIngredientId(ingredientId);
                        StockValue sv = new StockValue(
                                rs.getDouble("quantity"),
                                UnitEnum.valueOf(rs.getString("unit"))
                        );
                        sm.setValue(sv);
                        movements.add(sm);
                    }
                }
            }
            return movements;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public Ingredient saveIngredient(Ingredient toSave) {
        Connection conn = null;
        String upsertIngSql = """
                INSERT INTO ingredient (id, name, price, category) 
                VALUES (?, ?, ?, ?::category_enum)
                ON CONFLICT (id) DO UPDATE
                SET name = EXCLUDED.name,
                    price = EXCLUDED.price,
                    category = EXCLUDED.category
                RETURNING id
                """;
        try {
            conn = new DBConnection().getConnection();
            conn.setAutoCommit(false);
            Integer ingredientId;

            try (PreparedStatement ps = conn.prepareStatement(upsertIngSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                }
                ps.setString(2, toSave.getName());
                ps.setDouble(3, toSave.getPrice() != null ? toSave.getPrice() : 0.0);
                ps.setString(4, toSave.getCategory() != null ? toSave.getCategory().name() : "OTHER");

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    ingredientId = rs.getInt(1);
                }
            }

            saveStockMovements(conn, ingredientId, toSave.getStockMovementList());

            conn.commit();
            closeConnection(conn);
            conn = null;

            return findIngredientById(ingredientId);
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private void saveStockMovements(Connection conn, Integer ingredientId, List<StockMovement> movements) throws SQLException {
        if (movements == null || movements.isEmpty()) return;
        String sql = """
                INSERT INTO stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime)
                VALUES (?, ?, ?, ?::movement_type, ?::unit_type, ?)
                ON CONFLICT (id) DO NOTHING
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (StockMovement mov : movements) {
                if (mov.getId() != null) {
                    ps.setInt(1, mov.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "stock_movement", "id"));
                }
                ps.setInt(2, ingredientId);
                double qty = (mov.getValue() != null && mov.getValue().getQuantity() != null) ? mov.getValue().getQuantity() : 0.0;
                String unit = (mov.getValue() != null && mov.getValue().getUnit() != null) ? mov.getValue().getUnit().name() : "KG";

                ps.setDouble(3, qty);
                ps.setString(4, mov.getType().name());
                ps.setString(5, unit);
                ps.setTimestamp(6, Timestamp.from(mov.getCreationDatetime() != null ? mov.getCreationDatetime() : Instant.now()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public Order findOrderByReference(String reference) {
        Connection connection = null;
        String sqlOrder = "SELECT id, reference, creation_datetime FROM \"order\" WHERE reference = ?";
        try {
            connection = new DBConnection().getConnection();
            try (PreparedStatement ps = connection.prepareStatement(sqlOrder)) {
                ps.setString(1, reference);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Order order = new Order();
                        order.setId(rs.getInt("id"));
                        order.setReference(rs.getString("reference"));
                        order.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

                        order.setDishOrders(findDishOrdersByOrderId(order.getId()));
                        return order;
                    } else {
                        throw new RuntimeException("Commande introuvable pour la référence : " + reference);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    private List<DishOrder> findDishOrdersByOrderId(Integer orderId) {
        Connection connection = null;
        List<DishOrder> dishOrders = new ArrayList<>();
        String sql = "SELECT id, id_dish, quantity FROM dish_order WHERE id_order = ?";
        try {
            connection = new DBConnection().getConnection();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DishOrder dishOrder = new DishOrder();
                        dishOrder.setId(rs.getInt("id"));
                        dishOrder.setQuantity(rs.getInt("quantity"));
                        dishOrder.setDish(findDishById(rs.getInt("id_dish")));
                        dishOrders.add(dishOrder);
                    }
                }
            }
            return dishOrders;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public Order saveOrder(Order orderToSave) {
        Connection conn = null;
        try {
            conn = new DBConnection().getConnection();
            conn.setAutoCommit(false);

            checkStockAvailability(conn, orderToSave);

            int nextId = getNextSerialValue(conn, "\"order\"", "id");            String generatedRef = String.format("ORD%05d", nextId);
            orderToSave.setId(nextId);
            orderToSave.setReference(generatedRef);

            String sqlOrder = "INSERT INTO \"order\" (id, reference, creation_datetime) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                ps.setInt(1, nextId);
                ps.setString(2, generatedRef);
                ps.setTimestamp(3, Timestamp.from(
                        orderToSave.getCreationDatetime() != null ? orderToSave.getCreationDatetime() : Instant.now()
                ));                ps.executeUpdate();
            }

            String sqlDishOrder = "INSERT INTO dish_order (id_order, id_dish, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlDishOrder)) {
                for (DishOrder doReq : orderToSave.getDishOrders()) {
                    ps.setInt(1, nextId);
                    ps.setInt(2, doReq.getDish().getId());
                    ps.setInt(3, doReq.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            closeConnection(conn);
            conn = null;

            return findOrderByReference(generatedRef);

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw e;
        } finally {
            closeConnection(conn);
        }
    }

    private void checkStockAvailability(Connection conn, Order order) throws SQLException {
        for (DishOrder dishOrder : order.getDishOrders()) {
            for (DishIngredient di : dishOrder.getDish().getDishIngredients()) {
                double neededRaw = di.getQuantityRequired() * dishOrder.getQuantity();

                double neededInKg = UnitConvert.toKg(
                        di.getIngredient().getName(),
                        neededRaw,
                        di.getUnit()
                );

                double currentStockInKg = di.getIngredient().getStockValueAt(Instant.now());

                if (currentStockInKg < neededInKg) {
                    throw new RuntimeException("Stock insuffisant pour l'ingrédient : " + di.getIngredient().getName());
                }
            }
        }
    }


    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);
        try (PreparedStatement ps = conn.prepareStatement("SELECT nextval(?)")) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT pg_get_serial_sequence(?, ?)")) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String sql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 1) FROM %s), false)",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeQuery();
        }
    }
}