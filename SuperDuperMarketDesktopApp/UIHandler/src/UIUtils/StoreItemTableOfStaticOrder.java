package UIUtils;

import Enums.OrderType;
import Enums.PurchaseType;

public class StoreItemTableOfStaticOrder {

    public StoreItemTableOfStaticOrder(int serialNumber, String name, Double price, PurchaseType purchaseType) {//,double price, int soldItemsNumber ){
        this.serialNumber = serialNumber;
        this.name = name;
        this.price = price;
        this.purchaseType = purchaseType;

    }

    public int serialNumber;
    public String name = null;
    public Double price;
    public PurchaseType purchaseType;
    public String quantity;


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

    public String getQuantity() {
        return quantity;
    }

}