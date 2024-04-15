package org.vexillum;

public enum REGION {
    AFRICA(0),
    ASIA(1),
    EUROPE(2),
    NORTH_AMERICA(3),
    OCEANIA(4),
    SOUTH_AMERICA(5);

    private final int value;

    REGION(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}