package UIUtils;

import Enums.PurchaseType;

public class StoreItemTableOfDynamicOrder {

    public StoreItemTableOfDynamicOrder(int serialNumber, String name, PurchaseType purchaseType) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.purchaseType = purchaseType;

    }

    public int serialNumber;
    public String name = null;
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

    public String getQuantity() {
        return quantity;
    }

}

