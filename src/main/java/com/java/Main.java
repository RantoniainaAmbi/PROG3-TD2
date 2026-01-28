package com.java;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever repo = new DataRetriever();

        System.out.println("=== TEST 1 : Création d'une commande valide ===");
        try {
            Dish margherita = repo.findDishById(1);

            Order newOrder = new Order();
            newOrder.setCreationDatetime(Instant.now());

            List<DishOrder> items = new ArrayList<>();
            DishOrder item1 = new DishOrder();
            item1.setDish(margherita);
            item1.setQuantity(2);
            items.add(item1);

            newOrder.setDishOrders(items);

            Order saved = repo.saveOrder(newOrder);
            System.out.println("Commande créée avec succès !");
            System.out.println("Référence générée : " + saved.getReference());
            System.out.println("Montant HT : " + saved.getTotalAmountWithoutVAT() + "€");
            System.out.println("Montant TTC : " + saved.getTotalAmountWithVAT() + "€");

        } catch (Exception e) {
            System.err.println("Erreur Test 1 : " + e.getMessage());
        }

        System.out.println("\n=== TEST 2 : Recherche par référence ===");
        try {
            Order found = repo.findOrderByReference("ORD00001");
            System.out.println("Commande trouvée : " + found.getReference());
            System.out.println("Date : " + found.getCreationDatetime());
            found.getDishOrders().forEach(doReq ->
                    System.out.println("- " + doReq.getQuantity() + "x " + doReq.getDish().getName())
            );
        } catch (Exception e) {
            System.err.println("Erreur Test 2 : " + e.getMessage());
        }

        System.out.println("\n=== TEST 3 : Stock insuffisant (Doit lever une exception) ===");
        try {
            Dish deluxe = repo.findDishById(2);
            Order bigOrder = new Order();
            bigOrder.setCreationDatetime(Instant.now());

            DishOrder item = new DishOrder();
            item.setDish(deluxe);
            item.setQuantity(1000);

            bigOrder.setDishOrders(List.of(item));
            repo.saveOrder(bigOrder);

        } catch (RuntimeException e) {
            System.out.println("Succès du test d'erreur : " + e.getMessage());
        }
    }
}