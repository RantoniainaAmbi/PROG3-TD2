package com.java;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        try {
            RestaurantTable table1 = new RestaurantTable(1, 1);

            Instant debut = Instant.now();
            Instant fin = debut.plus(1, ChronoUnit.HOURS);
            TableOrder tableOrder = new TableOrder(table1, debut, fin);

            Order o = new Order();
            o.setReference("TEST-CHECK");
            o.setTableOrder(tableOrder);
            o.setDishOrders(new ArrayList<>());
            o.setCreationDatetime(Instant.now());

            System.out.println("--- TEST : DISPONIBILITÉ TABLE ---");
            System.out.println("Tentative sur la Table 1 à " + debut);

            dr.saveOrder(o);

        } catch (RuntimeException e) {
            System.out.println("\n[RÉSULTAT ATTENDU]");
            if (e.getMessage().contains("n'est pas disponible")) {
                System.out.println("✅ SUCCÈS : Le conflit a été détecté AVANT l'erreur SQL.");
                System.out.println("MESSAGE : " + e.getMessage());
            } else {
                System.out.println("❌ ERREUR persistante : " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}