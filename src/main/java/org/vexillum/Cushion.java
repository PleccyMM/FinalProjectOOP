package org.vexillum;

import javax.persistence.*;

/**
 * One of two {@code StockItem} children in the program
 * <p>
 * Utilises hibernate mapping
 */
@Entity
public class Cushion extends StockItem {
    private int cushionID;
    private boolean justCase;
    private CUSHION_SIZE size;
    private CUSHION_MATERIAL material;

    /**
     * Empty constructor only for hibernate
     */
    public Cushion() {}

    /**
     * Constructor primarily used for creation of a new version of the object
     */
    public Cushion(int cushionID, String isoID, int stockID) {
        this.cushionID = cushionID;
        this.isoID = isoID;
        this.stockID = stockID;
    }

    /**
     * Constructor only used for the {@code .clone()} method
     */
    public Cushion(String isoID, String name, int stockID, int amount, int totalAmount, int restock, int sizeID,
                   double costToProduce, int cushionID, boolean justCase, CUSHION_SIZE size, CUSHION_MATERIAL material) {
        this.isoID = isoID;
        this.name = name;
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
        return new Cushion(isoID, name, stockID, amount, totalAmount, restock, sizeID, costToProduce, cushionID, justCase, size, material);
    }

    @Override
    public double calculatePrice() {
        if (amount < 0) return costToProduce;

        double cost = 0;
        cost += size != null ? size.getValue() : 0;
        if (!justCase) cost += material != null ? material.getValue() : 0;
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cushion c)) return false;
        if (material != c.getMaterial()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int prime = 29;

        int result = 1;
        result = prime * result + material.hashCode();

        return prime * result + super.hashCode();
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
