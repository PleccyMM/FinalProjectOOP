package org.vexillum;

public enum REGION {
    AFRICA(0),
    ASIA(1),
    EUROPE(2),
    NORTH_AMERICA(3),
    OCEANIA(4),
    SOUTH_AMERICA(5);

    public final int value;
    private REGION(int value) {
        this.value = value;
    }
}