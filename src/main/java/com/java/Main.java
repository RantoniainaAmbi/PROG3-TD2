package com.java;


public class Main {
    public static void main(String[] args) {
        System.out.println("=== Vérification des Stocks Final (Objectif KG) ===");

        printResult("Laitue", 5.0, 2, UnitEnum.PCS);
        printResult("Tomate", 4.0, 5, UnitEnum.PCS);
        printResult("Poulet", 10.0, 4, UnitEnum.PCS);
        printResult("Chocolat", 3.0, 1, UnitEnum.L);
        printResult("Beurre", 2.5, 1, UnitEnum.L);
    }

    private static void printResult(String name, double initial, double outQty, UnitEnum unit) {
        double outInKg = UnitConvert.toKg(name, outQty, unit);
        double finalStock = initial - outInKg;
        System.out.println(name + " : " + initial + " - " + outInKg + " = " + finalStock + " KG");
    }
}