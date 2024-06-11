package org.vexillum;

import javax.persistence.*;

/**
 * One of two {@code StockItem} children in the program
 * <p>
 * Utilises hibernate mapping
 */
@Entity
public class Flag extends StockItem {
    private int flagID;
    private FLAG_MATERIAL material;
    private FLAG_HOIST hoist = FLAG_HOIST.NONE;
    private FLAG_SIZE size;

    /**
     * Empty constructor only for hibernate
     */
    public Flag() {}

    /**
     * Constructor primarily used for creation of a new version of the object
     */
    public Flag(int flagID, String isoID, int stockID) {
        this.flagID = flagID;
        this.isoID = isoID;
        this.stockID = stockID;
    }

    /**
     * Constructor only used for the {@code .clone()} method
     */
    public Flag(String isoID, String name, int stockID, int amount, int totalAmount, int restock, int sizeID,
                double costToProduce, int flagID, FLAG_MATERIAL material, FLAG_HOIST hoist, FLAG_SIZE size, boolean isNational) {
        this.isoID = isoID;
        this.name = name;
        this.stockID = stockID;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.restock = restock;
        this.sizeID = sizeID;
        this.costToProduce = costToProduce;
        this.flagID = flagID;
        this.material = material;
        this.hoist = hoist;
        this.size = size;
        this.isNational = isNational;
    }

    @Override
    public StockItem clone() {
        return new Flag(isoID, name, stockID, amount, totalAmount, restock, sizeID, costToProduce, flagID, material, hoist, size, isNational);
    }

    @Override
    public double calculatePrice() {
        if (isImport()) return costToProduce;

        double cost = 0;
        cost += size != null ? size.getValue() : 0;
        if (isNational) cost *= 0.9;
        cost += material != null ? material.getValue() : 0;
        cost += hoist != null ? hoist.getValue() : 0;
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        System.out.println("We're equal checking");
        if (!(o instanceof Flag f)) return false;
        if (hoist != null && f.getHoist() != null && hoist != f.getHoist()) return false;
        if (material != null && f.getMaterial() != null && material != f.getMaterial()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int prime = 31;

        int result = 1;
        result = prime * result + (material != null ? material.hashCode() : 0);
        result = prime * result + (hoist != null ? hoist.hashCode() : 0);

        return prime * result + super.hashCode();
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

    public FLAG_HOIST getHoist() {
        return hoist;
    }
    public void setHoist(FLAG_HOIST hoist) {
        this.hoist = hoist;
    }

    public FLAG_SIZE getSize() {
        return size;
    }
    public void setSize(FLAG_SIZE size) {
        this.size = size;
    }
    public boolean isSmall() {
        return size == FLAG_SIZE.HAND || size == FLAG_SIZE.DESK;
    }
}
