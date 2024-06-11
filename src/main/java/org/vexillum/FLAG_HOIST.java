package org.vexillum;

/**
 * An {@code enum} used to store information about flag hoists and their cost
 */
public enum FLAG_HOIST {
    NONE(0),
    FABRIC(50),
    METAL(250),
    WOODEN(500);

    private final int value;

    FLAG_HOIST(int value) {
        this.value = value;
    }

    public double getValue() {
        return value / 100.0f;
    }

    public static String getString(FLAG_HOIST f1) {
        String s = (f1.toString()).toUpperCase();
        return switch (s) {
            case "NONE" -> "None (\u00A30.00)";
            case "FABRIC" -> "Fabric Rings (\u00A30.50)";
            case "METAL" -> "Metal Rings (\u00A32.50)";
            default -> "Wooden Toggles (\u00A35.00)";
        };
    }
    public static FLAG_HOIST fromString(String s1) {
        String s = s1.toUpperCase();
        return switch (s) {
            case "None (\u00A30.00)" -> NONE;
            case "Fabric Rings (\u00A30.50)" -> FABRIC;
            case "Metal Rings (\u00A32.50)" -> METAL;
            default -> WOODEN;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case FABRIC -> "Fabric Rings (\u00A30.50)";
            case METAL -> "Metal Rings (\u00A32.50)";
            case WOODEN -> "Wooden Toggles (\u00A35.00)";
            default -> "None (\u00A30.00)";
        };
    }
}