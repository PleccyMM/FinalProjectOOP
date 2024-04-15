package org.vexillum;

public class Cushion extends StockItem {
    private boolean justCase;
    private CUSHION_SIZE size;

    public Cushion() {}

    @Override
    public float calculatePrice() {
        float cost = size.getValue();
        if (justCase) cost -= 8;
        return cost;
    }

    public boolean isJustCase() {
        return justCase;
    }
    public void setJustCase(boolean justCase) {
        this.justCase = justCase;
    }

    public CUSHION_SIZE getSize() {
        return size;
    }
    public void setSize(CUSHION_SIZE size) {
        this.size = size;
    }
}
