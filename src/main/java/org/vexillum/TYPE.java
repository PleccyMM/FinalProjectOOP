package org.vexillum;

public enum TYPE {
    NATIONAL(0),
    INTERNATIONAL(1),
    PRIDE(2);

    private final int value;

    TYPE(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}