package com.java;


public class Main {
    public static void main(String[] args) {
        System.out.println("=== Vérification des Stocks Final (Objectif KG) ===");

        printResult("Laitue", 5.0, 2, UnitEnum.PCS);   // Attendu: 4.0 KG
        printResult("Tomate", 4.0, 5, UnitEnum.PCS);   // Attendu: 3.5 KG
        printResult("Poulet", 10.0, 4, UnitEnum.PCS);  // Attendu: 9.5 KG
        printResult("Chocolat", 3.0, 1, UnitEnum.L);   // Attendu: 2.6 KG
        printResult("Beurre", 2.5, 1, UnitEnum.L);     // Attendu: 2.3 KG
    }

    private static void printResult(String name, double initial, double outQty, UnitEnum unit) {
        double outInKg = UnitConvert.toKg(name, outQty, unit);
        double finalStock = initial - outInKg;
        System.out.println(name + " : " + initial + " - " + outInKg + " = " + finalStock + " KG");
    }
}