package com.java;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever repo = new DataRetriever();
        System.out.println("--- Scénario 1 : Récupération et calcul de marge ---");

        try {
            Dish salade = repo.findDishById(1);
            System.out.print("Plat : " + salade.getName() + " | Prix vente : " + salade.getPrice());
            System.out.print(" | Coût ingrédients : " + salade.getDishCost());
            System.out.print(" | Marge brute : " + salade.getGrossMargin() + "\n");
        } catch (Exception e) {
            System.out.print("Erreur inattendue sur Salade : " + e.getMessage() + "\n");
        }

        try {
            Dish riz = repo.findDishById(3);
            System.out.print("Plat : " + riz.getName() + " | Prix vente : " + riz.getPrice() + "\n");
            System.out.print("Tentative de calcul de marge pour le riz : " + riz.getGrossMargin() + "\n");
        } catch (RuntimeException e) {
            System.out.print("Comportement attendu (Exception) : " + e.getMessage() + "\n");
        }

        System.out.println("\n--- Scénario 2 : Sauvegarde et Mise à jour de prix ---");

        try {
            Dish rizToUpdate = repo.findDishById(3);
            rizToUpdate.setPrice(4500.0);

            Dish updatedRiz = repo.saveDish(rizToUpdate);
            System.out.print("Mise à jour réussie : " + updatedRiz.getName());
            System.out.print(" | Nouveau prix : " + updatedRiz.getPrice());
        } catch (Exception e) {
            System.out.print("Erreur lors de la mise à jour : " + e.getMessage() + "\n");
        }

        try {
            List<Ingredient> ingredientsPizza = new ArrayList<>();
            ingredientsPizza.add(new Ingredient("Farine", 100.0, CategoryEnum.OTHER));
            ingredientsPizza.add(new Ingredient("Fromage", 300.0, CategoryEnum.OTHER));

            Dish pizza = new Dish("Pizza Margherita", DishTypeEnum.MAIN, 8000.0, ingredientsPizza);

            Dish savedPizza = repo.saveDish(pizza);
            System.out.print("Création réussie - ID : " + savedPizza.getId());
            System.out.print(" | Plat : " + savedPizza.getName());
            System.out.print(" | Marge brute : " + savedPizza.getGrossMargin() + "\n");
        } catch (Exception e) {
            System.out.print("Erreur lors de la création : " + e.getMessage() + "\n");
            throw new RuntimeException(e);
        }

    }
}