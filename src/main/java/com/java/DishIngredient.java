package com.java;

public class DishIngredient {
    private int id;
    private Ingredient ingredient;
    private Double quantityRequired;
    private UnitEnum unit;

    public DishIngredient() {}

    public DishIngredient(int id,Ingredient ingredient, Double quantityRequired, UnitEnum unit) {
        this.id = id;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public Double getQuantityRequired() { return quantityRequired; }
    public void setQuantityRequired(Double quantityRequired) { this.quantityRequired = quantityRequired; }

    public UnitEnum getUnit() { return unit; }
    public void setUnit(UnitEnum unit) { this.unit = unit; }
}