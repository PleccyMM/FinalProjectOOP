package org.vexillum;

import javax.persistence.*;

@MappedSuperclass
public abstract class StockItem {
    protected String isoID;
    protected int stockID;
    protected int amount = 1;
    protected int totalAmount;
    protected int restock;
    protected int sizeID;

    public abstract float calculatePrice();

    public StockItem() {}

    public StockItem(String isoID) {
        this.isoID = isoID;
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
}
