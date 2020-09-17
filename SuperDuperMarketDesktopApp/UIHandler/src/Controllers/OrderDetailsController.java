package Controllers;

import Enums.PurchaseType;
import Handlers.LocationHandler;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import Models.*;
import UIUtils.OrderDetailsItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsController {
    @FXML private AnchorPane orderDetailsPane;
    @FXML private ListView orderDetailItemList;
    @FXML private ScrollPane orderDetailsScroll;
    @FXML private Label  orderDetailsLabel;
    private SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    private StoreHandler storeHandler = new StoreHandler();
    private LocationHandler locationHandler = new LocationHandler();
    private TableView<OrderDetailsItem> orderitemsTable = new TableView<OrderDetailsItem> ();
    @FXML private Button continueButton;
    @FXML  Button cancelButton;

    public void showorderDetails(SuperDuperMarket superDuperMarket, Order order, Pane textPane) {

        List<OrderDetailsItem> orderItemTable = new ArrayList<>();
        // add column if the item added on sale
        for (OrderItem oi : order.orderItems) {
            Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);
            OrderDetailsItem orderDetailsItem = new OrderDetailsItem();
            orderDetailsItem.itemID = item.serialNumber;
            orderDetailsItem.name = item.name;
            orderDetailsItem.purchaseType = item.purchaseType;
            orderDetailsItem.price = oi.price;

            if (oi.quantityObject.KGQuantity > 0){
                double quantiy = oi.quantityObject.KGQuantity;

                orderDetailsItem.quantity = quantiy;
                orderDetailsItem.price = quantiy * oi.price;
            }
            else{
                int quantiy = oi.quantityObject.integerQuantity;
                orderDetailsItem.quantity = quantiy;
                orderDetailsItem.price = quantiy * oi.price;
            }
            Store store = storeHandler.getStoreById(superDuperMarket, oi.storeId);
            double distance = locationHandler.calculateDistanceOfTwoLocations(store.Location, order.CustomerLocation);
            orderDetailsItem.distanceFromTheStore = distance;
            orderDetailsItem.pricePerKilometer = store.PPK;
            orderDetailsItem.shippingCost = order.deliveryPriceByStore.get(store.serialNumber);
            orderItemTable.add(orderDetailsItem);


            continueButton.setOnAction(new EventHandler() {

                @Override
                public void handle(Event event) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Thank you for purchasing at super duper market");
                    superDuperMarket.Orders.addOrder(superDuperMarket, order);

                    alert.showAndWait();

                    textPane.getChildren().clear();
                    Stage stage = (Stage) continueButton.getScene().getWindow();
                    stage.close();
                }
            });
            cancelButton.setOnAction(new EventHandler() {

                @Override
                public void handle(Event event) {

                    textPane.getChildren().clear();

                }
            });

        }

        //orderDetailsPane.getChildren().add(orderItemTable);
        BuildFxTableViewOrderItems(orderItemTable);
        orderitemsTable.setLayoutY(100);
        orderDetailsScroll.setContent(orderDetailsPane);

    }

    void showOrderDetails(Order order) throws IOException {


    }
    private void BuildFxTableViewOrderItems(List<OrderDetailsItem> itemTable) {

        ObservableList data = FXCollections.observableList(itemTable);
        orderitemsTable.setItems(data);

        TableColumn serialCol = new TableColumn("ItemID");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("itemID"));

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn purchaseTypeCol = new TableColumn("PurchaseType");
        purchaseTypeCol.setCellValueFactory(new PropertyValueFactory<>("purchaseType"));

        TableColumn PriceCol = new TableColumn("Price");
        PriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn quantityCol = new TableColumn("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn totalPricePerItemCol = new TableColumn("TotalPricePerItem");
        totalPricePerItemCol.setCellValueFactory(new PropertyValueFactory<>("totalPricePerItem"));

        TableColumn distanceFromTheStoreCol = new TableColumn("DistanceFromTheStore");
        distanceFromTheStoreCol.setCellValueFactory(new PropertyValueFactory<>("distanceFromTheStore"));

        TableColumn pricePerKilometerCol = new TableColumn("PricePerKilometer");
        pricePerKilometerCol.setCellValueFactory(new PropertyValueFactory<>("pricePerKilometer"));

        TableColumn shippingCostCol = new TableColumn("ShippingCost");
        shippingCostCol.setCellValueFactory(new PropertyValueFactory<>("shippingCost"));

        orderitemsTable.getColumns().setAll(serialCol, nameCol, purchaseTypeCol,PriceCol,quantityCol,shippingCostCol,pricePerKilometerCol,distanceFromTheStoreCol,totalPricePerItemCol);

        orderitemsTable.setPrefWidth(700);
        orderitemsTable.setPrefHeight(400);
        orderitemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Label label = new Label("Order Details");
        label.setTextFill(Color.DARKBLUE);
        label.setFont(Font.font("Calibri", FontWeight.BOLD, 32));

        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(label);

        // Status message text
        Text actionStatus = new Text();
        actionStatus.setFill(Color.FIREBRICK);

        // Vbox
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(hb, orderitemsTable, actionStatus);
        orderDetailsPane.getChildren().add(vbox);
    }

}
