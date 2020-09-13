package Controllers;

import Enums.OperatorTypeOfSale;
import Enums.PurchaseType;
import Handlers.LocationHandler;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class OrderDetailsController {
    @FXML private AnchorPane orderDetailsPane;
    @FXML private ListView orderDetailItemList;

    private SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    private StoreHandler storeHandler = new StoreHandler();
    private LocationHandler locationHandler = new LocationHandler();

    public void showorderDetails(SuperDuperMarket superDuperMarket, Order order) {
        OrderDetailsItem orderDetailsItem =new OrderDetailsItem();
        orderDetailsItem.itemID = 1;
        orderDetailsItem.name = "fdsfsdf";

        ObservableList<OrderDetailsItem> fruitList = FXCollections.<OrderDetailsItem>observableArrayList();
        fruitList.add(orderDetailsItem);
        orderDetailItemList.setOrientation(Orientation.HORIZONTAL);
        // Set the Size of the ListView
        orderDetailItemList.setPrefSize(200, 100);
        // Add the items to the ListView
        orderDetailItemList.getItems().addAll(fruitList);
/*
        for (OrderItem oi : order.orderItems) {
            Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);
            System.out.printf("%-25s  %-25s  %-25s  %-25s", "Serial number:" + oi.itemId, " Name:" + item.name, " Purchase type:" + item.purchaseType.toString(), " Price:" + oi.price);

            if (oi.quantityObject.KGQuantity > 0){
                double quantiy = oi.quantityObject.KGQuantity;
                System.out.println("Quantity:" + quantiy);
                System.out.println("Total price per item:" + quantiy * oi.price);
            }
            else{
                int quantiy = oi.quantityObject.integerQuantity;
                System.out.println("Quantity:" + quantiy);
                System.out.println("Total price per item:" + quantiy * oi.price);
            }
            Store store = storeHandler.getStoreById(superDuperMarket, oi.storeId);
            double distance = locationHandler.calculateDistanceOfTwoLocations(store.Location, order.CustomerLocation);
            System.out.println("The distance from the store: " + String.format("%.2f", distance));
            System.out.println("Price per kilometer: " + store.PPK);
            System.out.println("Shipping cost: " + String.format("%.2f", order.deliveryPriceByStore.get(store.serialNumber)));

        }
*/
    }

    void showOrderDetails(Order order) throws IOException {


    }


}
class OrderDetailsItem {


    public int serialNumber;
    public int itemID;
    public String name;
    PurchaseType purchaseType;
    double price;
    double quantity;
    double totalPricePerItem;
    double distanceFromTheStore;
    double pricePerKilometer;
    double ShippingCost;


}
