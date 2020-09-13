package UIUtils;

import Enums.PurchaseType;

public class StoreItemTable {

    public StoreItemTable(int serialNumber, String name, Double price, String purchaseType){//,double price, int soldItemsNumber ){
        this.serialNumber = serialNumber;
        this.name = name;
        this.price =price;
        this.purchaseType = PurchaseType.valueOf(purchaseType.toUpperCase());

    }

    public int serialNumber;
    public String name = null;
    public Double price;
    public PurchaseType purchaseType;



    public String getName() {
        return name;
    }
    public String getPurchaseType() {
        return purchaseType.toString();
    }

    public int getSerialNumber() {
        return serialNumber;
    }
    public Double getPrice() {
        return price;
    }
}