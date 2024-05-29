package org.vexillum;

/**
 * An {@code enum} used to store information about cushion fillings and their cost
 */
public enum CUSHION_MATERIAL {
    EMPTY(0),
    FOAM(800),
    POLYESTER(900),
    FEATHERS(1100),
    COTTON(1200);

    private final int value;

    CUSHION_MATERIAL(int value) {
        this.value = value;
    }

    public double getValue() {
        return value / 100.0f;
    }

    public static CUSHION_MATERIAL getType(String s1) {
        String s = s1.toUpperCase();
        return switch (s) {
            case "FOAM" -> FOAM;
            case "POLYESTER" -> POLYESTER;
            case "FEATHERS" -> FEATHERS;
            case "COTTON" -> COTTON;
            default -> EMPTY;
        };
    }
}