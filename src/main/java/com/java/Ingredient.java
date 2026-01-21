package com.java;

import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;

    public Ingredient(String name, Integer id, CategoryEnum category, Double price) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.price = price;
    }

    public Ingredient() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                '}';
    }
}
