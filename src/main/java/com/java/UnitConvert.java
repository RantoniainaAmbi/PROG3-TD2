package com.java;

import java.util.Map;

public class UnitConvert {
    private static final Map<String, Map<UnitEnum, Double>> CONVERSION_TABLE = Map.of(
            "Tomate", Map.of(UnitEnum.PCS, 10.0),
            "Laitue", Map.of(UnitEnum.PCS, 2.0),
            "Chocolat", Map.of(UnitEnum.PCS, 10.0, UnitEnum.L, 2.5),
            "Poulet", Map.of(UnitEnum.PCS, 8.0),
            "Beurre", Map.of(UnitEnum.PCS, 4.0, UnitEnum.L, 5.0)
    );

    public static double toKg(String ingredientName, double quantity, UnitEnum unit) {
        if (unit == UnitEnum.KG) return quantity;

        Map<UnitEnum, Double> ingredientRatios = CONVERSION_TABLE.get(ingredientName);

        if (ingredientRatios == null || !ingredientRatios.containsKey(unit)) {
            throw new RuntimeException("Conversion Impossible");
        }

        return quantity / ingredientRatios.get(unit);
    }
}