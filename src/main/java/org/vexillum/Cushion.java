package org.vexillum;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cushion extends StockItem {
    private int cushionID;
    private boolean justCase;
    private CUSHION_SIZE size;

    public Cushion() {}

    public Cushion(int cushionID, String isoID, int stockID) {
        this.cushionID = cushionID;
        this.isoID = isoID;
        this.stockID = stockID;
    }

    @Override
    public float calculatePrice() {
        float cost = size.getValue();
        if (justCase) cost -= 8;
        return cost;
    }

    @Id
    public int getCushionID() {
        return cushionID;
    }
    public void setCushionID(int cushionID) {
        this.cushionID = cushionID;
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
