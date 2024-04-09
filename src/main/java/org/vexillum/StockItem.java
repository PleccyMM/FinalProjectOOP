package org.vexillum;

public abstract class StockItem {
    protected String isoID;
    protected int stockID;
    protected int amount = 1;
    public abstract float calculatePrice();


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
}
