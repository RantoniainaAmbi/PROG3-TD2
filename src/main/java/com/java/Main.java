package com.java;

import com.java.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public class Main {
        public static void main(String[] args) {
            DataRetriever dr = new DataRetriever();

            Integer idIngredient = 1;
            Instant now = Instant.now();

            System.out.println("--- TEST COMPARAISON JAVA vs SQL (TD5) ---");


            Ingredient ingredient = dr.findIngredientById(idIngredient);
            double stockJava = ingredient.getStockValueAt(now);

            System.out.println("Stock calculé par Java : " + stockJava);


            StockValue stockSQL = dr.getStockValueAt(now, idIngredient);

            System.out.println("Stock calculé par SQL  : " + stockSQL.getQuantity() + " " + stockSQL.getUnit());


            if (stockJava == stockSQL.getQuantity()) {
                System.out.println(" SUCCÈS : Les résultats sont identiques !");
            } else {
                System.out.println(" DIFFÉRENCE : Vérifiez vos données.");
                System.out.println("Note : Sans UnitConvert, assurez-vous que tous les mouvements de cet ingrédient ont la même unité (ex: tout en KG).");
            }

            Integer idPlat = 1;
            Double cout = dr.getDishCost(idPlat);
            Double marge = dr.getGrossMargin(idPlat);

            System.out.println("Coût de revient du plat : " + cout);
            System.out.println("Marge brute du plat     : " + marge);
            Instant debut = Instant.parse("2026-01-01T00:00:00Z");
            Instant fin = Instant.parse("2026-01-31T23:59:59Z");

            System.out.println("\n--- TEST 2 : COMPARAISON DES PÉRIODICITÉS ---");
            Instant start = Instant.parse("2026-01-01T00:00:00Z");
            Instant end = Instant.now();

            System.out.println("Évolution par JOUR :");
            dr.getStockEvolution(1, "day", start, end).forEach((d, v) -> System.out.println(d + " : " + v));

            System.out.println("\nÉvolution par MOIS :");
            dr.getStockEvolution(1, "month", start, end).forEach((d, v) -> System.out.println(d + " : " + v));

        }
}