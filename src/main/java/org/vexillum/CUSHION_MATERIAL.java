package org.vexillum;

public enum CUSHION_MATERIAL {
    EMPTY(0),
    FOAM(0),
    POLYESTER(100),
    FEATHERS(300),
    COTTON(400);

    private final int value;

    CUSHION_MATERIAL(int value) {
        this.value = value;
    }

    public float getValue() {
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