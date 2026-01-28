package com.java;

import java.time.Instant;
import java.util.List;

public class Order {
    private int id;
    private String reference;
    private Instant creationDateTime;
    private List<DishOrder> dishOrders;

    public Order(int id, String reference, Instant creationDateTime, List<DishOrder> dishOrders) {
        this.id = id;
        this.reference = reference;
        this.creationDateTime = creationDateTime;
        this.dishOrders = dishOrders;
    }

    public Order() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }

    public Double getTotalAmountWithoutVAT() {
        return dishOrders.stream()
                .mapToDouble(dishOrder -> {
                    Double price = dishOrder.getDish().getPrice();
                    if (price == null) {
                        throw new RuntimeException("Le plat " + dishOrder.getDish().getName() + " n'a pas de prix de vente défini.");
                    }
                    return price * dishOrder.getQuantity();
                })
                .sum();
    }

    public Double getTotalAmountWithVAT() {
        return getTotalAmountWithoutVAT() * 1.2;
    }

    public Instant getCreationDatetime() {
        return creationDateTime;
    }

    public void setCreationDatetime(Instant now) {
        this.creationDateTime = creationDateTime;

    }
}
