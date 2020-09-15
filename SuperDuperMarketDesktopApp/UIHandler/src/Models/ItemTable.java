package Models;

import Enums.PurchaseType;

public class ItemTable {

    public ItemTable(int serialNumber, String name, Double price, PurchaseType purchaseType){//,double price, int soldItemsNumber ){
        this.serialNumber = serialNumber;
        this.name = name;
        this.price =price;
        this.purchaseType =purchaseType;

        //this.soldItemsNumber = soldItemsNumber;
    }

    public int serialNumber;
    public String name = null;
    public Double price;
    public String quantity;
    public PurchaseType purchaseType;

    //public int soldItemsNumber;


    public String getName() {
        return name;
    }
    public int getSerialNumber() {
        return serialNumber;
    }
    public Double getPrice() {
        return price;
    }
    public String getQuantity() {
        return quantity;
    }
}
