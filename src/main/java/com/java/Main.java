package com.java;

public class Main {
    public static void main(String[] args) {
        DataRetriever repo = new DataRetriever();
        int[] idsToTest = {1, 2, 3, 4, 5};

        System.out.println("--- DÉBUT DES TESTS (Many-to-Many) ---");

        for (int id : idsToTest) {
            try {
                Dish dish = repo.findDishById(id);
                System.out.println("Plat : " + dish.getName());

                System.out.println(" - Coût production : " + dish.getDishCost());

                System.out.println(" - Marge brute : " + dish.getGrossMargin());

            } catch (RuntimeException e) {
                System.out.println(" - " + e.getMessage());
            } catch (Exception e) {
                System.out.println(" - Erreur technique : " + e.getMessage());
            }
            System.out.println("-----------------------------------");
        }
    }
}