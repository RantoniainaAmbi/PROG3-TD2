package com.java;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection;

    public DataRetriever() {
        this.dbConnection = new DBConnection();
    }

    public Dish findDishById(Integer id) {
        String query = "SELECT d.id as d_id, d.name as d_name, d.dish_type, d.price as d_price, " +
                "i.id as i_id, i.name as i_name, i.price as i_price, i.category " +
                "FROM dish d " +
                "LEFT JOIN ingredient i ON d.id = i.id_dish " +
                "WHERE d.id = ?";

        Dish dish = null;
        Connection conn = null;

        try {
            conn = dbConnection.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        if (dish == null) {
                            dish = new Dish();
                            dish.setId(rs.getInt("d_id"));
                            dish.setName(rs.getString("d_name"));
                            dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));

                            Double dPrice = rs.getObject("d_price") != null ? rs.getDouble("d_price") : null;
                            dish.setPrice(dPrice);
                        }

                        int iId = rs.getInt("i_id");
                        if (iId > 0) {
                            Ingredient ingredient = new Ingredient();
                            ingredient.setId(iId);
                            ingredient.setName(rs.getString("i_name"));
                            ingredient.setPrice(rs.getDouble("i_price"));

                            String catStr = rs.getString("category");
                            if(catStr != null) ingredient.setCategory(CategoryEnum.valueOf(catStr));

                            ingredient.setDish(dish);
                            dish.addIngredient(ingredient);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.attemptCloseConnection(conn);
        }

        if (dish == null) throw new RuntimeException("Plat non trouvé avec l'id: " + id);

        return dish;
    }

    public List<Ingredient> findIngredients(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();
        int offset = (page - 1) * size;
        String query = "SELECT * FROM ingredient ORDER BY id ASC LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, size);
            stmt.setInt(2, Math.max(0, offset));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ingredients.add(mapResultSetToIngredient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        List<Ingredient> createdIngredients = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbConnection.getDBConnection();
            conn.setAutoCommit(false);

            for (Ingredient ing : newIngredients) {
                try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM ingredient WHERE name = ?")) {
                    check.setString(1, ing.getName());
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            throw new RuntimeException("L'ingrédient existe déjà: " + ing.getName());
                        }
                    }
                }

                int nextId = 1;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM ingredient")) {
                    if (rs.next()) {
                        nextId = rs.getInt(1) + 1;
                    }
                }

                try (PreparedStatement ins = conn.prepareStatement("INSERT INTO ingredient (id, name, price, category) VALUES (?, ?, ?, ?::category_enum)")) {
                    ins.setInt(1, nextId);
                    ins.setString(2, ing.getName());
                    ins.setDouble(3, ing.getPrice() != null ? ing.getPrice() : 0.0);
                    ins.setString(4, ing.getCategory() != null ? ing.getCategory().name() : "OTHER");
                    ins.executeUpdate();

                    ing.setId(nextId);
                    createdIngredients.add(ing);
                }
            }
            conn.commit();
            return createdIngredients;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        } finally {
            dbConnection.attemptCloseConnection(conn);
        }
    }
    public Dish saveDish(Dish dishToSave) {
        Connection conn = null;
        try {
            conn = dbConnection.getDBConnection();
            conn.setAutoCommit(false);

            int dishId;
            if (dishToSave.getId() > 0) {
                dishId = dishToSave.getId();
                String sql = "UPDATE dish SET name = ?, dish_type = ?::dish_type, price = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, dishToSave.getName());
                    stmt.setString(2, dishToSave.getDishType().name());

                    if (dishToSave.getPrice() != null) stmt.setDouble(3, dishToSave.getPrice());
                    else stmt.setNull(3, java.sql.Types.DOUBLE);

                    stmt.setInt(4, dishId);
                    stmt.executeUpdate();
                }
            } else {
                int nextId = 1;
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT MAX(id) FROM dish")) {
                    if (rs.next()) nextId = rs.getInt(1) + 1;
                }
                dishId = nextId;
                String sql = "INSERT INTO dish (id, name, dish_type, price) VALUES (?, ?, ?::dish_type, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, dishId);
                    stmt.setString(2, dishToSave.getName());
                    stmt.setString(3, dishToSave.getDishType().name());

                    if (dishToSave.getPrice() != null) stmt.setDouble(4, dishToSave.getPrice());
                    else stmt.setNull(4, java.sql.Types.DOUBLE);

                    stmt.executeUpdate();
                }
                dishToSave.setId(dishId);
            }

            try (PreparedStatement unlink = conn.prepareStatement("UPDATE ingredient SET id_dish = NULL WHERE id_dish = ?")) {
                unlink.setInt(1, dishId);
                unlink.executeUpdate();
            }

            if (dishToSave.getIngredients() != null) {
                for (Ingredient ing : dishToSave.getIngredients()) {
                    if (ing == null || ing.getName() == null) continue;

                    try (PreparedStatement search = conn.prepareStatement("SELECT id FROM ingredient WHERE name = ?")) {
                        search.setString(1, ing.getName());
                        ResultSet rs = search.executeQuery();

                        if (rs.next()) {
                            int ingId = rs.getInt(1);
                            try (PreparedStatement link = conn.prepareStatement("UPDATE ingredient SET id_dish = ? WHERE id = ?")) {
                                link.setInt(1, dishId);
                                link.setInt(2, ingId);
                                link.executeUpdate();
                            }
                        } else {
                            int nextIngId = 1;
                            try (Statement st = conn.createStatement();
                                 ResultSet rs2 = st.executeQuery("SELECT MAX(id) FROM ingredient")) {
                                if (rs2.next()) nextIngId = rs2.getInt(1) + 1;
                            }
                            String insSql = "INSERT INTO ingredient (id, name, price, category, id_dish) VALUES (?, ?, ?, ?::category_enum, ?)";
                            try (PreparedStatement ins = conn.prepareStatement(insSql)) {
                                ins.setInt(1, nextIngId);
                                ins.setString(2, ing.getName());
                                ins.setDouble(3, ing.getPrice() != null ? ing.getPrice() : 0.0);
                                ins.setString(4, ing.getCategory() != null ? ing.getCategory().name() : "OTHER");
                                ins.setInt(5, dishId);
                                ins.executeUpdate();
                            }
                        }
                    }
                }
            }

            conn.commit();

            return findDishById(dishId);

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Erreur lors de saveDish: " + e.getMessage(), e);
        } finally {
            dbConnection.attemptCloseConnection(conn);
        }
    }



    public List<Dish> findDishsByIngredientName(String ingredientName) {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT DISTINCT id_dish FROM ingredient WHERE name ILIKE ? AND id_dish IS NOT NULL";
        Connection conn = null;

        try {
            conn = dbConnection.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + ingredientName + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        dishes.add(findDishById(rs.getInt("id_dish")));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.attemptCloseConnection(conn);
        }

        return dishes;
    }

    public List<Ingredient> findingredientsByCriteria(String name, CategoryEnum cat, String dName, int page, int size) {
        List<Ingredient> results = new ArrayList<>();
        String sql = "SELECT i.id, i.name, i.price, i.category, i.id_dish " +
                "FROM ingredient i " +
                "LEFT JOIN dish d ON i.id_dish = d.id " +
                "WHERE 1=1 ";

        if (name != null) sql += "AND i.name ILIKE ? ";
        if (cat != null) sql += "AND i.category = ?::category_enum ";
        if (dName != null) sql += "AND d.name ILIKE ? ";

        sql += "ORDER BY i.id LIMIT ? OFFSET ?";

        Connection conn = null;
        try {
            conn = dbConnection.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int idx = 1;
                if (name != null) stmt.setString(idx++, "%" + name + "%");
                if (cat != null) stmt.setString(idx++, cat.name());
                if (dName != null) stmt.setString(idx++, "%" + dName + "%");

                stmt.setInt(idx++, size);
                int offset = (page - 1) * size;
                stmt.setInt(idx, Math.max(0, offset));

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(mapResultSetToIngredient(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.attemptCloseConnection(conn);
        }
        return results;
    }

    private Ingredient mapResultSetToIngredient(ResultSet rs) throws SQLException {
        Ingredient ing = new Ingredient();
        ing.setId(rs.getInt("id"));
        ing.setName(rs.getString("name"));
        ing.setPrice(rs.getDouble("price"));
        ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
        return ing;
    }
}