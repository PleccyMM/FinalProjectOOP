package org.vexillum;

public enum CUSHION_SIZE {
    SMALL(1000),
    MEDIUM(1800),
    LARGE(2200),
    LONG(1200);

    private final int value;

    CUSHION_SIZE(int value) {
        this.value = value;
    }

    public float getValue() {
        return value / 100.0f;
    }

    public static String getString(CUSHION_SIZE f) {
        return switch (f) {
            case SMALL -> "45x45cm";
            case MEDIUM -> "55x55cm";
            case LARGE -> "60x60cm";
            case LONG -> "50x30cm";
        };
    }
    public static CUSHION_SIZE fromString(String s) {
        return switch (s) {
            case "45x45cm" -> SMALL;
            case "55x55cm" -> MEDIUM;
            case "60x60cm" -> LARGE;
            default -> LONG;
        };
    }
}