package org.vexillum;

import javax.persistence.*;

public class Flag extends StockItem {
    private int flagID;
    private FLAG_MATERIAL material;

    public Flag() {}

    @Override
    public float calculatePrice() {
        return 8 + material.getValue();
    }

    @Id
    public int getFlagID() {
        return flagID;
    }
    public void setFlagID(int flagID) {
        this.flagID = flagID;
    }

    public FLAG_MATERIAL getMaterial() {
        return material;
    }
    public void setMaterial(FLAG_MATERIAL material) {
        this.material = material;
    }
}
