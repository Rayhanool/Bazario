package com.bazario.app.model;

import java.util.Locale;

/** Selling unit for a Firebase product. Price and stock are both measured in this unit. */
public final class ProductUnit {
    private ProductUnit() {}

    public static final String KG = "kg";
    public static final String G = "g";
    public static final String LITER = "liter";
    public static final String ML = "ml";
    public static final String PIECE = "piece";
    public static final String PACKET = "packet";
    public static final String BOTTLE = "bottle";

    public static String normalize(String value) {
        if (value == null) return PIECE;
        String s = value.trim().toLowerCase(Locale.US);
        if (s.equals("kg") || s.equals("kgs") || s.equals("kilogram") || s.equals("kilograms")) return KG;
        if (s.equals("g") || s.equals("gram") || s.equals("grams")) return G;
        if (s.equals("l") || s.equals("liter") || s.equals("litre") || s.equals("liters") || s.equals("litres")) return LITER;
        if (s.equals("ml") || s.equals("milliliter") || s.equals("millilitre") || s.equals("milliliters")) return ML;
        if (s.equals("packet") || s.equals("pack") || s.equals("packets")) return PACKET;
        if (s.equals("bottle") || s.equals("bottles")) return BOTTLE;
        return PIECE;
    }

    /** Used only for old Firebase documents that do not yet have a unit field. */
    public static String detectLegacy(String unit, String name, String category) {
        if (unit != null && !unit.trim().isEmpty()) return normalize(unit);
        String s = ((name == null ? "" : name) + " " + (category == null ? "" : category)).toLowerCase(Locale.US);
        if (s.contains("rice") || s.contains("alu") || s.contains("potato") || s.contains("onion")
                || s.contains("garlic") || s.contains("ginger") || s.contains("tomato") || s.contains("dal")
                || s.contains("lentil") || s.contains("flour") || s.contains("atta") || s.contains("sugar")) return KG;
        if (s.contains("oil") || s.contains("shampoo") || s.contains("juice") || s.contains("drink")) return BOTTLE;
        if (s.contains("packet") || s.contains("chips") || s.contains("biscuit") || s.contains("noodle")) return PACKET;
        return PIECE;
    }

    public static String label(String unit) {
        String u = normalize(unit);
        switch (u) {
            case KG: return "kg";
            case G: return "g";
            case LITER: return "L";
            case ML: return "ml";
            case PACKET: return "packet";
            case BOTTLE: return "bottle";
            default: return "piece";
        }
    }

    public static String quantityText(long quantity, String unit) {
        return quantity + " " + label(unit);
    }
}
