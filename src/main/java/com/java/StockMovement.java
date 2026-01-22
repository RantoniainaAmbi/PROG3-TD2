package com.java;

import java.time.Instant;

public class StockMovement {
    private Integer id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDatetime;
    private Integer ingredientId;

    public StockMovement(Integer id, StockValue value, MovementTypeEnum type, Instant creationDatetime, Integer ingredientId) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.creationDatetime = creationDatetime;
        this.ingredientId = ingredientId;
    }

    public StockMovement() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public StockValue getValue() { return value; }
    public void setValue(StockValue value) { this.value = value; }

    public MovementTypeEnum getType() { return type; }
    public void setType(MovementTypeEnum type) { this.type = type; }

    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }

    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }
}