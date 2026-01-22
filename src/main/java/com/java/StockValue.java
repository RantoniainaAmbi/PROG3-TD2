package com.java;

public class StockValue {
    private Double quantity;
    private UnitEnum unit;

    public StockValue(Double quantity, UnitEnum unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public UnitEnum getUnit() { return unit; }
    public void setUnit(UnitEnum unit) { this.unit = unit; }
}