package UIUtils;

import Enums.PurchaseType;
import Models.Item;
import Models.OrderItem;

public class StoreItemTable{

    public StoreItemTable(Double price, Item item){//,double price, int soldItemsNumber ){
        this.serialNumber = item.serialNumber;
        this.name = item.name;
        this.price =price;
        this.purchaseType = item.purchaseType;
        this.Item = item;
    }

    public int serialNumber;
    public String name = null;
    public Double price;
    public PurchaseType purchaseType;
    public Models.Item Item;

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