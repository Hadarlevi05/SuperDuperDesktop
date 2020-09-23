package UIUtils;

import Enums.PurchaseType;

public class OrderDetailsItem {

    public int itemID;
    public String name;
    public PurchaseType purchaseType;
    //public double price;
    public double quantity;
    public double totalPricePerItem;
    public double totalPrice;
    public boolean participatesInSale;
    public boolean boughtOnSale;

    //public double distanceFromTheStore;
    //public double pricePerKilometer;
    //public double shippingCost;
/*    @Override
    public String toString(){
        DecimalFormat df = new DecimalFormat("#.00");

        return ("Item ID: " + this.itemID + " Name: " + this.name + " purchase Type" + this.purchaseType.toString()
                + " price" + this.price + " quantity" + this.quantity + " Total Price Per Item" + this.totalPricePerItem
                + " distance From The Store" + df.format(this.distanceFromTheStore) + " Price Per Kilometer" + this.pricePerKilometer + " Shipping Cost" + df.format(this.shippingCost)
        );
    }*/

    public int getItemID() {
        return itemID;
    }
    public String getName() {
        return name;
    }
    public PurchaseType getPurchaseType() {
        return purchaseType;
    }
    public double getQuantity() {
        return quantity;
    }
    public boolean getBoughtOnSale() {
        return boughtOnSale;
    }
    public double getTotalPricePerItem() {
        return totalPricePerItem;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public boolean getParticipatesInSale() {
        return participatesInSale;
    }



}
