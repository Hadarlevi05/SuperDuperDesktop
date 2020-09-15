package UIUtils;

import Enums.PurchaseType;

import java.text.DecimalFormat;

public class OrderDetailsItem {

    public int serialNumber;
    public int itemID;
    public String name;
    public PurchaseType purchaseType;
    public double price;
    public double quantity;
    public double totalPricePerItem;
    public double distanceFromTheStore;
    public double pricePerKilometer;
    public double shippingCost;
    @Override
    public String toString(){
        DecimalFormat df = new DecimalFormat("#.00");

        return ("Item ID: " + this.itemID + " Name: " + this.name + " purchase Type" + this.purchaseType.toString()
                + " price" + this.price + " quantity" + this.quantity + " Total Price Per Item" + this.totalPricePerItem
                + " distance From The Store" + df.format(this.distanceFromTheStore) + " Price Per Kilometer" + this.pricePerKilometer + " Shipping Cost" + df.format(this.shippingCost)
        );
    }

    public int getItemID() {
        return itemID;
    }
    public String getName() {
        return name;
    }
    public PurchaseType getPurchaseType() {
        return purchaseType;
    }
    public double getPrice() {
        return price;
    }
    public double getQuantity() {
        return quantity;
    }
    public double getTotalPricePerItem() {
        return totalPricePerItem;
    }
    public double getDistanceFromTheStore() {
        return distanceFromTheStore;
    }
    public double getPricePerKilometer() {
        return pricePerKilometer;
    }
    public double getShippingCost() {
        return shippingCost;
    }


}
