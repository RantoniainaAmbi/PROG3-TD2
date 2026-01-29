package com.java;

import java.time.Instant;

public class TableOrder {
    private RestaurantTable table;
    private Instant arrivalDatetime;
    private Instant departureDatetime;

    public TableOrder(RestaurantTable table, Instant arrivalDatetime, Instant departureDatetime) {
        this.table = table;
        this.arrivalDatetime = arrivalDatetime;
        this.departureDatetime = departureDatetime;
    }

    public RestaurantTable getTable() { return table; }
    public Instant getArrivalDatetime() { return arrivalDatetime; }
    public Instant getDepartureDatetime() { return departureDatetime; }
}