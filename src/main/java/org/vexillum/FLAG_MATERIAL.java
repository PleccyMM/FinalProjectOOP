package org.vexillum;

public enum FLAG_MATERIAL {
    PAPER(0),
    POLYESTER(100),
    NYLON(300);

    private final int value;

    FLAG_MATERIAL(int value) {
        this.value = value;
    }

    public float getValue() {
        return value / 100.0f;
    }

    public static FLAG_MATERIAL getType(String s1) {
        String s = s1.toUpperCase();
        return switch (s) {
            case "PAPER" -> PAPER;
            case "POLYESTER" -> POLYESTER;
            default -> NYLON;
        };
    }
}