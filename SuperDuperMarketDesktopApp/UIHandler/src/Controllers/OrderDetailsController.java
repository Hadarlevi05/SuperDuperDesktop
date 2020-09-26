package Controllers;

import Enums.OperatorTypeOfSale;
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

import javax.swing.text.LayoutQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsController {
    @FXML
    private AnchorPane orderDetailsPane;
    @FXML
    private ListView orderDetailItemList;
    @FXML
    private ScrollPane orderDetailsScroll;
    @FXML
    private Label orderDetailsLabel;
    private SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    private StoreHandler storeHandler = new StoreHandler();
    private LocationHandler locationHandler = new LocationHandler();
    private TableView<OrderDetailsItem> orderitemsTable = new TableView<OrderDetailsItem>();
    @FXML
    private VBox orderDetailsVbox = new VBox();
    private List<OrderDetailsComponent> childs = new ArrayList<>();
    @FXML
    private Button continueButton;
    @FXML
    Button cancelButton;

    public void showorderDetails(SuperDuperMarket superDuperMarket, Order order, Pane textPane) {
        orderDetailsScroll.setVvalue(1.0);
        Label storeLabel = new Label("Stores: ");
        storeLabel.setStyle(" -fx-text-Alignment: CENTER; -fx-font-size: 20 ;-fx-font-family:Tahoma;-fx-font-weight: bold ");
        orderDetailsVbox.getChildren().add(storeLabel);
        orderDetailsVbox.setPadding(new Insets(10, 10, 10, 10));
        orderDetailsVbox.setSpacing(10);

        for (int storeID : order.storesID) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Resources/OrderDetailsPerStore.fxml"));
                orderDetailsVbox.getChildren().add(fxmlLoader.load());
                OrderDetailsComponent orderDetailsComponent = fxmlLoader.getController();
                orderDetailsComponent.SetOrderDetails(superDuperMarket, order, storeID);
                childs.add(orderDetailsComponent);

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
                        Stage stage = (Stage) continueButton.getScene().getWindow();
                        stage.close();
                    }
                });
/*                saleComponent.setLayoutY(height * 200);
                saleComponent.setCheckboxSelectCallback(selectedItemInDiscount -> {
                    updateselectedItemInDiscount(selectedItemInDiscount);

                    if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ONE_OF) {
                        SetOneOf(selectedItemInDiscount.itemID, selectedItemInDiscount.selected);
                    } else if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ALL_OR_NOTHING) {
                        SetAllOrNothing(selectedItemInDiscount.selected);
                    }
                });*/
                //height++;
                //Checkbox tb = (Checkbox)saleComponent.lookup("#cbxSelectDiscount");

            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        Label totalItemsPrice = new Label("Total Price of items: " + String.format("%.2f", order.totalItemsPrice));
        Label deliveryPrice = new Label("Total Delivery price: " + String.format("%.2f", order.deliveryPrice));
        Label totalPrice = new Label("Total Price of order: " + String.format("%.2f", order.totalPrice));

        totalItemsPrice.setStyle(" -fx-text-Alignment: CENTER; -fx-font-size: 20 ;-fx-font-family:Tahoma;-fx-font-weight: bold ");
        deliveryPrice.setStyle(" -fx-text-Alignment: CENTER; -fx-font-size: 20 ;-fx-font-family:Tahoma;-fx-font-weight: bold ");
        totalPrice.setStyle(" -fx-text-Alignment: CENTER; -fx-font-size: 20 ;-fx-font-family:Tahoma;-fx-font-weight: bold ");


        orderDetailsVbox.getChildren().add(totalItemsPrice);
        orderDetailsVbox.getChildren().add(deliveryPrice);
        orderDetailsVbox.getChildren().add(totalPrice);
        orderDetailsScroll.setContent(orderDetailsVbox);


/*
        List<OrderDetailsItem> orderItemTable = new ArrayList<>();
*/
        // add column if the item added on sale
/*        for (OrderItem oi : order.orderItems) {
            Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);
            OrderDetailsItem orderDetailsItem = new OrderDetailsItem();
            orderDetailsItem.itemID = item.serialNumber;
            orderDetailsItem.name = item.name;
            orderDetailsItem.purchaseType = item.purchaseType;
            orderDetailsItem.totalPrice = oi.price;

            if (oi.quantityObject.KGQuantity > 0) {
                double quantiy = oi.quantityObject.KGQuantity;

                orderDetailsItem.quantity = quantiy;
                orderDetailsItem.totalPrice = quantiy * oi.price;
            } else {
                int quantiy = oi.quantityObject.integerQuantity;
                orderDetailsItem.quantity = quantiy;
                orderDetailsItem.totalPrice = quantiy * oi.price;
            }
            Store store = storeHandler.getStoreById(superDuperMarket, oi.storeId);
            double distance = locationHandler.calculateDistanceOfTwoLocations(store.Location, order.CustomerLocation);
            orderDetailsItem.distanceFromTheStore = distance;
            orderDetailsItem.pricePerKilometer = store.PPK;
            orderDetailsItem.shippingCost = order.deliveryPriceByStore.get(store.serialNumber);
            orderItemTable.add(orderDetailsItem);


        }

        //orderDetailsPane.getChildren().add(orderItemTable);
        BuildFxTableViewOrderItems(orderItemTable);*/
        //orderitemsTable.setLayoutY(100);
        //orderDetailsScroll.setContent(orderDetailsPane);

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

        orderitemsTable.getColumns().setAll(serialCol, nameCol, purchaseTypeCol, PriceCol, quantityCol, shippingCostCol, pricePerKilometerCol, distanceFromTheStoreCol, totalPricePerItemCol);

        orderitemsTable.setPrefWidth(700);
        orderitemsTable.setPrefHeight(400);
        orderitemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Label label = new Label("Order Details");
        //label.setTextFill(Color.DARKBLUE);
        label.setFont(Font.font("Calibri", FontWeight.BOLD, 32));

        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(label);

        // Status message text
        Text actionStatus = new Text();
        //actionStatus.setFill(Color.FIREBRICK);

        // Vbox
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(hb, orderitemsTable, actionStatus);
        orderDetailsPane.getChildren().add(vbox);
    }

}
