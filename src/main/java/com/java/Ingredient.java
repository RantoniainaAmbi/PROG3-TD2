package com.java;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private List<StockMovement> stockMovementList = new ArrayList<>();

    public Ingredient(String name, Integer id, CategoryEnum category, Double price) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.price = price;
    }

    public Ingredient(Integer id, String name, CategoryEnum category, Double price, List<StockMovement> stockMovementList) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockMovementList = stockMovementList;
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

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    public Double getStockValueAt(Instant t) {
        if (stockMovementList == null || stockMovementList.isEmpty()) {
            return 0.0;
        }

        return stockMovementList.stream()
                .filter(m -> m.getCreationDatetime() != null && !m.getCreationDatetime().isAfter(t))
                .mapToDouble(m -> {
                    double qty = (m.getValue() != null && m.getValue().getQuantity() != null)
                            ? m.getValue().getQuantity()
                            : 0.0;

                    if (m.getType() == MovementTypeEnum.IN) {
                        return qty;
                    } else {
                        return -qty;
                    }
                })
                .sum();
    }
}
