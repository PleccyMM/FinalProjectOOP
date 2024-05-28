package org.vexillum;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cushion extends StockItem {
    private int cushionID;
    private boolean justCase;
    private CUSHION_SIZE size;
    private CUSHION_MATERIAL material;

    public Cushion() {}

    public Cushion(int cushionID, String isoID, int stockID) {
        this.cushionID = cushionID;
        this.isoID = isoID;
        this.stockID = stockID;
    }


    public Cushion(String isoID, int stockID, int amount, int totalAmount, int restock, int sizeID, double costToProduce,
                   int cushionID, boolean justCase, CUSHION_SIZE size, CUSHION_MATERIAL material) {
        this.isoID = isoID;
        this.stockID = stockID;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.restock = restock;
        this.sizeID = sizeID;
        this.costToProduce = costToProduce;
        this.cushionID = cushionID;
        this.justCase = justCase;
        this.size = size;
        this.material = material;
    }

    @Override
    public StockItem clone() {
        return new Cushion(isoID, stockID, amount, totalAmount, restock, sizeID, costToProduce, cushionID, justCase, size, material);
    }

    @Override
    public double calculatePrice() {
        if (amount < 0) return DatabaseControl.getPrice(this.getSizeID());

        double cost = size.getValue();
        if (!justCase) cost += material.getValue();
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

    public CUSHION_MATERIAL getMaterial() {
        return material;
    }
    public void setMaterial(CUSHION_MATERIAL material) {
        this.material = material;
    }
}
