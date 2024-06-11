package org.vexillum;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public abstract class StockItem implements Comparable<StockItem> {
    protected String isoID;
    protected String name;
    protected int stockID;
    //A negative amount means that it's being imported and not exported
    protected int amount = 1;
    protected int totalAmount;
    protected int restock;
    protected int sizeID;
    protected double costToProduce;
    protected boolean isNational = false;

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

    @Override
    public int compareTo(StockItem o) {
        int i = Integer.compare(Integer.signum(amount), Integer.signum(o.getAmount()));
        int j = name.compareTo(o.getName());
        return i != 0 ? i : j != 0 ? j : Integer.compare(sizeID, o.getSizeID());
    }

    /**
     * 2 objects can have differences and still considered equal. {@code equals} only checks if both have the same: name,
     * stockID, sizeID, and if they are being imported or exported {@code (Integer.signum(amount)}. {@code Flag} and {@code Cushion}
     * both override and extend this
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        return equalCheck(o);
    }
    /**
     * Just the super version of equals, the child checks are not included
     */
    public boolean baseEquals(Object o) {
        return equalCheck(o);
    }
    /**
     * {@code equalCheck} is split off as to allow the merge functionality to happen, since merging requires the ability
     * to both do an equals check using only the super and also with the children's overridden versions. By having {@code baseEquals}
     * and {@code equals} simply use a single helper function code is not repeated and the functionality is present
     */
    private boolean equalCheck(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockItem stockItem)) return false;

        if (!Objects.equals(name, stockItem.getName())) return false;
        if (stockID != stockItem.getStockID()) return false;
        if (sizeID != stockItem.getSizeID()) return false;
        if (Integer.signum(amount) != Integer.signum(stockItem.getAmount())) return false;

        return true;
    }

    /**
     * The hashcode is generated using the same properties that are checked to determine equality, and like {@code equals},
     * all children for {@code StockItem} override and extend this. Hashcodes using the same value of equals is intentional
     * as their primary role is to produce unique IDs for boxes in the {@code BasketController} screen, and merging automatically
     * occurs meaning that the hashcode needs to shadow {@code equals}
     * @return an integer unique to the object, within the confines of what {@code equals} see as unique objects
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + name.hashCode();
        result = prime * result + stockID;
        result = prime * result + sizeID;
        result = prime * result + Integer.signum(amount);

        return result;
    }

    @Id
    public String getIsoID() {
        return isoID;
    }
    public void setIsoID(String isoID) {
        this.isoID = isoID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public boolean isImport() {
        return amount < 0;
    }
    public boolean isExport() {
        return amount > 0;
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

    public boolean isNational() {
        return isNational;
    }
    public void setNational(boolean national) {
        isNational = national;
    }
}
