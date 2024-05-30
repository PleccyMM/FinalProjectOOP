package org.vexillum;

import javax.persistence.*;

@MappedSuperclass
public abstract class StockItem {
    protected String isoID;
    protected int stockID;
    //A negative amount means that it's being imported and not exported
    protected int amount = 1;
    protected int totalAmount;
    protected int restock;
    protected int sizeID;
    //This value is only used when printing to file, and is left unassigned most of the time
    protected double costToProduce;

    /**
     * Used to create a new version of {@code StockItem}, when it is needed not by reference
     *
     * @return an exact new copy of itself
     */
    public abstract StockItem clone();

    /**
     * Calculates the price for this {@code StockItem}
     *
     * @return a decimal value for the price of a single version of the item
     */
    public abstract double calculatePrice();

    public StockItem() {}

    @Override
    public String toString() {
        return "\nTotal Amount: " + totalAmount + "\nRestock Level: " + restock + "\n";
    }

    @Id
    public String getIsoID() {
        return isoID;
    }
    public void setIsoID(String isoID) {
        this.isoID = isoID;
    }

    public int getStockID() {
        return stockID;
    }
    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public int getPrintAmount() {
        if (amount < 0) return amount * -1;
        return amount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getRestock() {
        return restock;
    }
    public void setRestock(int restock) {
        this.restock = restock;
    }

    public int getSizeID() {
        return sizeID;
    }
    public void setSizeID(int sizeID) {
        this.sizeID = sizeID;
    }

    public double getCostToProduce() {
        return costToProduce;
    }
    public void setCostToProduce(double costToProduce) {
        this.costToProduce = costToProduce;
    }
}
