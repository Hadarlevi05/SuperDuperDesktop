package Controllers;

import Handlers.LocationHandler;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import Models.*;
import UIUtils.OrderDetailsItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsComponent {


    @FXML
    private TableView<OrderDetailsItem> itemsInOrderDetails;
    @FXML
    private Label DelivrtyPriceLbl;
    @FXML
    private Label storeDistLbl;
    @FXML
    private Label storePpkLbl;
    @FXML
    private Label storeNameLbl;
    @FXML
    private Label storeNumLbl;
    private static DecimalFormat df = new DecimalFormat("0.00");

    SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    StoreHandler storeHandler = new StoreHandler();
    LocationHandler locationHandler = new LocationHandler();

    public void SetOrderDetails(SuperDuperMarket superDuperMarket, Order order, int storeID) {
        List<OrderDetailsItem> orderItemTable = new ArrayList<>();

        Store store = storeHandler.getStoreById(superDuperMarket, storeID);
        double distance = locationHandler.calculateDistanceOfTwoLocations(store.Location, order.CustomerLocation);
        storeDistLbl.setText(String.valueOf(df.format(distance)));
        storePpkLbl.setText(String.valueOf(store.PPK));
        DelivrtyPriceLbl.setText(df.format(order.deliveryPriceByStore.get(store.serialNumber)).toString());
        storeNameLbl.setText(store.name);
        storeNumLbl.setText(String.valueOf(store.serialNumber));
        for (OrderItem oi : order.orderItems) {
            if (oi.storeId == store.serialNumber) {
                OrderDetailsItem orderOtemDetails = getOrderDetailsOfOrderItem(superDuperMarket, storeID, orderItemTable, oi, false);
                orderItemTable.add(orderOtemDetails);
            }
        }
        for (OrderItem oi : order.orderItemsFromSales) {
            if (oi.storeId == store.serialNumber) {
                OrderDetailsItem orderOtemDetails = getOrderDetailsOfOrderItem(superDuperMarket, storeID, orderItemTable, oi, true);
                orderItemTable.add(orderOtemDetails);
            }
        }

        ObservableList data = FXCollections.observableList(orderItemTable);
        itemsInOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemID"));
        itemsInOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        itemsInOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("purchaseType"));
        itemsInOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemsInOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("totalPricePerItem"));
        itemsInOrderDetails.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        itemsInOrderDetails.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("boughtOnSale"));


        itemsInOrderDetails.setItems(data);
    }

    private OrderDetailsItem getOrderDetailsOfOrderItem(SuperDuperMarket superDuperMarket, int storeID, List<OrderDetailsItem> orderItemTable, OrderItem oi, Boolean boughtOnSale) {

        OrderDetailsItem orderDetailsItem = new OrderDetailsItem();
        orderDetailsItem.boughtOnSale = boughtOnSale;
        if (oi.storeId == storeID) {
            Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);

            orderDetailsItem.itemID = item.serialNumber;
            orderDetailsItem.name = item.name;
            orderDetailsItem.purchaseType = item.purchaseType;
            orderDetailsItem.totalPrice = oi.price;

            if (oi.quantityObject.KGQuantity > 0) {
                double quantiy = oi.quantityObject.KGQuantity;

                orderDetailsItem.quantity = quantiy;
                orderDetailsItem.totalPrice = quantiy * oi.price;
                if (!orderDetailsItem.boughtOnSale) {
                    orderDetailsItem.totalPricePerItem = oi.price;
                } else {
                    double totalPricePerItem = orderDetailsItem.totalPrice / orderDetailsItem.quantity;
                    orderDetailsItem.totalPricePerItem = Double.parseDouble((String.format("%.2f", totalPricePerItem)));
                }
            } else {
                int quantiy = oi.quantityObject.integerQuantity;
                orderDetailsItem.quantity = quantiy;
                if (!orderDetailsItem.boughtOnSale) {
                    orderDetailsItem.totalPrice = quantiy * oi.price;
                    orderDetailsItem.totalPricePerItem = oi.price;
                } else {
                    double totalPricePerItem = orderDetailsItem.totalPrice / orderDetailsItem.quantity;
                    orderDetailsItem.totalPricePerItem = Double.parseDouble((String.format("%.2f", totalPricePerItem)));
                }
            }
        }
        return orderDetailsItem;
    }
}
