package com.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private Double price;
    private String name;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients = new ArrayList<>();

    public Double getDishCost() {
        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return 0.0;
        }

        return dishIngredients.stream()
                .filter(di -> di.getIngredient() != null && di.getQuantityRequired() != null)
                .mapToDouble(di -> {
                    Double unitPrice = di.getIngredient().getPrice();
                    return (unitPrice != null ? unitPrice : 0.0) * di.getQuantityRequired();
                })
                .sum();
    }

    public Double getGrossMargin() {
        if (this.price == null) {
            throw new RuntimeException("X Exception (prix NULL)");
        }
        return this.price - getDishCost();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public List<DishIngredient> getDishIngredients() { return dishIngredients; }
    public void setDishIngredients(List<DishIngredient> dishIngredients) { this.dishIngredients = dishIngredients; }

    public void addDishIngredient(DishIngredient dishIngredient) {
        this.dishIngredients.add(dishIngredient);
    }
}