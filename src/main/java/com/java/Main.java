package com.java;

import com.java.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Main {
    public static void main(String[] args) {
        DataRetriever repo = new DataRetriever();

        System.out.println("--- TD4 : Gestion des Stocks ---");

        Instant targetDate = LocalDateTime.of(2024, 1, 6, 12, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        System.out.println("Calcul du stock à la date : " + targetDate);

        int[] ids = {1, 2, 3, 4, 5};

        for (int id : ids) {
            try {
                Ingredient ing = repo.findIngredientById(id);
                Double stock = ing.getStockValueAt(targetDate);

                System.out.println("Ingrédient : " + ing.getName() +
                        " | Stock calculé : " + stock +
                        " " + (ing.getStockMovementList().isEmpty() ? "" : ing.getStockMovementList().get(0).getValue().getUnit()));
            } catch (Exception e) {
                System.out.println("Erreur ID " + id + ": " + e.getMessage());
            }
        }
    }
}