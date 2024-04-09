package org.vexillum;

public class Cushion extends StockItem {
    private boolean justCase;

    public Cushion() {}

    @Override
    public float calculatePrice() {
        return 0;
    }

    public boolean isJustCase() {
        return justCase;
    }
    public void setJustCase(boolean justCase) {
        this.justCase = justCase;
    }
}
