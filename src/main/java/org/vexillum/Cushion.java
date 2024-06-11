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
                   double costToProduce, int cushionID, CUSHION_SIZE size, CUSHION_MATERIAL material, boolean isNational) {
        this.isoID = isoID;
        this.name = name;
        this.stockID = stockID;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.restock = restock;
        this.sizeID = sizeID;
        this.costToProduce = costToProduce;
        this.cushionID = cushionID;
        this.size = size;
        this.material = material;
        this.isNational = isNational;
    }

    @Override
    public StockItem clone() {
        return new Cushion(isoID, name, stockID, amount, totalAmount, restock, sizeID, costToProduce, cushionID, size, material, isNational);
    }

    @Override
    public double calculatePrice() {
        if (isImport()) return costToProduce;

        double cost = 0;
        cost += size != null ? size.getValue() : 0;
        if (isNational) cost *= 0.9;
        if (material != CUSHION_MATERIAL.EMPTY) cost += material != null ? material.getValue() : 0;
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
        return material == CUSHION_MATERIAL.EMPTY;
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
