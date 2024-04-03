package org.vexillum;

public enum TYPE {
    NATIONAL(0),
    INTERNATIONAL(1),
    PRIDE(2);

    public final int value;
    private TYPE(int value) {
        this.value = value;
    }
}