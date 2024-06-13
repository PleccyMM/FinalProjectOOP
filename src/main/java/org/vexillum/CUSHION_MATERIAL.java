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

    @Override
    public String toString() {
        return switch (this) {
            case FOAM -> "Foam (\u00A38.00)";
            case POLYESTER -> "Polyester (\u00A39.00)";
            case FEATHERS -> "Feathers (\u00A311.00)";
            case COTTON -> "Cotton (\u00A312.00)";
            default -> "No Filling";
        };
    }
}