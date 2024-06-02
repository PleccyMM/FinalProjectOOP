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
                double costToProduce, int flagID, FLAG_MATERIAL material, FLAG_HOIST hoist, FLAG_SIZE size) {
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
    }

    @Override
    public StockItem clone() {
        return new Flag(isoID, name, stockID, amount, totalAmount, restock, sizeID, costToProduce, flagID, material, hoist, size);
    }

    @Override
    public double calculatePrice() {
        if (amount < 0) return DatabaseControl.getPrice(this.getSizeID());

        double cost = size.getValue();
        cost += material.getValue();
        cost += hoist.getValue();
        return cost;
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
}
