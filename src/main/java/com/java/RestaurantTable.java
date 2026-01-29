package com.java;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RestaurantTable {
    private int id;
    private int number;
    private List<Order> orders = new ArrayList<>();

    public RestaurantTable(int id, int number) {
        this.id = id;
        this.number = number;
    }

    public boolean isAvailableAt(Instant t) {
        for (Order order : orders) {
            TableOrder info = order.getTableOrder();
            if (info != null) {
                boolean isOccupied = !t.isBefore(info.getArrivalDatetime()) && t.isBefore(info.getDepartureDatetime());
                if (isOccupied) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getId() { return id; }
    public int getNumber() { return number; }
    public List<Order> getOrders() { return orders; }
    public void addOrder(Order order) { this.orders.add(order); }
}