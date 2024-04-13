package org.vexillum;

public enum FLAG_SIZE {
    HAND(250),
    DESK(400),
    SMALL(600),
    MEDIUM(800),
    LARGE(1800);

    private final int value;

    FLAG_SIZE(int value) {
        this.value = value;
    }

    public float getValue() {
        return value / 100.0f;
    }

    public static String getString(FLAG_SIZE f) {
        return switch (f) {
            case HAND -> "Hand";
            case DESK -> "Desk";
            case SMALL -> "90x60cm";
            case MEDIUM -> "150x90cm";
            case LARGE -> "240x150cm";
        };
    }
    public static FLAG_SIZE fromString(String s) {
        return switch (s) {
            case "Hand" -> HAND;
            case "Desk" -> DESK;
            case "90x60cm" -> SMALL;
            case "150x90cm" -> MEDIUM;
            default -> LARGE;
        };
    }
}