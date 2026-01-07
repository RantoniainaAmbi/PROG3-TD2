package com.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<Ingredient> ingredients = new ArrayList<>();

    public Dish(int id, String name, DishTypeEnum dishType, Double price, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.ingredients = (ingredients != null) ? ingredients : new ArrayList<>();
    }


    public Dish() {
    }

    public Dish(String name, DishTypeEnum dishType, Double price, List<Ingredient> ingredients) {
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.ingredients = (ingredients != null) ? ingredients : new ArrayList<>();
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public Double getPrice() {
        return price;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) return 0.0;
        return ingredients.stream()
                .filter(Objects::nonNull)
                .mapToDouble(i -> i.getPrice() != null ? i.getPrice() : 0.0)
                .sum();
    }
    public Double getGrossMargin() {
        if (this.price == null || this.price == 0) { // Vérifiez bien le null
            throw new RuntimeException("Prix non fixé !");
        }
        return this.price - getDishCost();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = (ingredients != null) ? ingredients : new ArrayList<>();
    }

    public void addIngredient(Ingredient ingredient) {
        if (ingredient != null) {
            this.ingredients.add(ingredient);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return id == dish.id &&
                Objects.equals(name, dish.name) &&
                dishType == dish.dishType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", price=" + (price != null ? price : "N/A") +
                ", ingredientsCount=" + (ingredients != null ? ingredients.size() : 0) +
                '}';
    }
}