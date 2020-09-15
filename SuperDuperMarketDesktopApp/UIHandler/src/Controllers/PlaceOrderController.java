package Controllers;

import Enums.OrderType;
import Enums.PurchaseType;
import Handlers.OrderDetailsHandler;
import Handlers.OrderManager;
import Handlers.StoreHandler;
import Models.*;
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
import javafx.scene.control.Label;
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
import java.net.URL;
import java.util.*;

public class PlaceOrderController {
    private Accordion accodionPane;

    @FXML
    private ComboBox<Customer> CustomerComboBox = new ComboBox<Customer>();
    private DatePicker purchaseDate = new DatePicker();
    private ComboBox<OrderType> orderType = new ComboBox<OrderType>();
    private ComboBox<Store> selectStore = new ComboBox<Store> ();
    private TableView<ItemTable> itemsTable = new TableView<ItemTable> ();
    private StoreHandler storeHandler = new StoreHandler();
    private Button continueButton = new Button("Continue");
    private OrderManager orderManager = new OrderManager();
    OrderDetailsHandler orderDetailsHandler = new OrderDetailsHandler();

    void placeOrder(SuperDuperMarket superDuperMarket, Pane textPane) {
        selectStore.setPromptText("Select Store");
        CustomerComboBox.setPromptText("Select customer");
        selectStore.setPromptText("Select store");
        final Customer[] selectedCustomer = new Customer[1];
        final Date[] orderDate = new Date[1];
        final Store[] store = new Store[1];

        Order order = new Order();
        purchaseDate.setDisable(true);
        orderType.setDisable(true);
        selectStore.setDisable(true);
        continueButton.setDisable(true);

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/PlaceOrderScreen2.fxml");
        fxmlLoader.setLocation(url);
        try{
            accodionPane =  fxmlLoader.load(url.openStream());
            ObservableList<Customer> customerDetails = FXCollections.observableArrayList();
            for (Customer customer:
                 superDuperMarket.Customers) {
                customerDetails.add(customer);
            }
            CustomerComboBox.setItems(customerDetails);


            textPane.getChildren().clear();
            textPane.getChildren().add(CustomerComboBox);
            accodionPane.prefWidthProperty().bind(textPane.widthProperty());
            CustomerComboBox.setOnAction (new EventHandler() {
                @Override
                public void handle(Event event) {
                    selectedCustomer[0] = CustomerComboBox.getValue();
                    purchaseDate.setDisable(false);
                }
            });


            purchaseDate.setOnAction(event -> {
                orderDate[0] = java.sql.Date.valueOf(purchaseDate.getValue());
                orderType.setDisable(false);
            });

            orderType.setPromptText("Select order type");
            ObservableList<OrderType> orderTypes = FXCollections.observableArrayList();
            orderTypes.addAll(EnumSet.allOf(OrderType.class));

            orderType.setItems(orderTypes);
            orderType.setOnAction(new EventHandler() {

                @Override
                public void handle(Event event) {
                    OrderType orderType = PlaceOrderController.this.orderType.getValue();
                    continueButton.setDisable(false);

                    switch (orderType){
                        case STATIC:
                            selectStore.setDisable(false);
                            ObservableList<Store> storeDetailsForOrder = FXCollections.observableArrayList();
                            for (Store store: superDuperMarket.Stores) {
                                storeDetailsForOrder.add(store);
                            }

                            selectStore.setItems(storeDetailsForOrder);
                            selectStore.setOnAction(new EventHandler(){

                                @Override
                                public void handle(Event event) {

                                    List<ItemTable> itemTable = new ArrayList<>();

                                    for (Item item:superDuperMarket.Items) {

                                        OrderItem oi = storeHandler.GetOrderItemByItemId(selectStore.getValue(), item.serialNumber);
                                        if (oi!=null){
                                            itemTable.add(new ItemTable(item.serialNumber,item.name,oi.price,item.purchaseType));

                                        }else{
                                            itemTable.add(new ItemTable(item.serialNumber,item.name, null,item.purchaseType));

                                        }
                                    }

                                    store[0] = selectStore.getValue();
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Shipping cost from the store you selected:" + store[0].PPK, ButtonType.OK);
                                    alert.showAndWait();

                                    BuildFxTableViewItems(itemTable);

                                    textPane.getChildren().add(itemsTable);
                                    itemsTable.setOnMouseClicked(new EventHandler(){
                                        @Override
                                        public void handle(Event event) {
                                            ItemTable selected = itemsTable.getSelectionModel().getSelectedItem();
                                            if (selected.price == null){
                                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The store you selected does not sell this item", ButtonType.OK);
                                                alert.showAndWait();
                                            }
                                            else{
                                                Dialog dialog = new TextInputDialog();
                                                //dialog.setTitle(titleTxt);
                                                dialog.setHeaderText("Please insert the required quantity");
/*

                                                ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                                                dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
*/

                                                Optional<String> result = dialog.showAndWait();
                                                if (result.isPresent()) {
                                                    selected.quantity = result.get();
                                                    itemsTable.refresh();
                                                    OrderItem oi = new OrderItem(selected.serialNumber, selected.price, store[0].serialNumber);
                                                    QuantityObject qo = null;
                                                    if (selected.purchaseType == PurchaseType.QUANTITY){
                                                        qo = new QuantityObject(Integer.parseInt(selected.quantity),0);
                                                    }
                                                    else {
                                                        qo = new QuantityObject(0, Double.parseDouble(selected.quantity));
                                                    }
                                                    orderDetailsHandler.updateOrderDetails(superDuperMarket, order, oi, store[0], selectedCustomer[0].location, orderDate[0], qo);
                                                }
                                            }
                                        }
                                    });

                                    /*itemsTable.getColumns().add();
                                    itemsTable.getColumns().add();

                                    for (Item item:superDuperMarket.Items) {
                                        OrderItem oi = storeHandler.GetOrderItemByItemId(store, item.serialNumber);
                                        itemsTable.getItems().add();
                                    }*/
                            }});

                            break;
                        case DYNAMIC:
                            break;
                    }
                    continueButton.setOnAction(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            try {
                                List<Discount> sales = orderManager.checkForSales(superDuperMarket,order);
                                if (sales.size() > 0){
                                    try {
                                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Resources/SaleScreen.fxml"));
                                        Scene scene = new Scene(fxmlLoader.load());
                                        Stage stage = new Stage();
                                        stage.setTitle("Anchor Pane Example");
                                        stage.setScene(scene);
                                        stage.show();
                                        SaleController saleController = fxmlLoader.getController();

                                        saleController.setRefreshOrderCallback(selectedOffers -> {
                                            stage.hide();
                                            orderDetailsHandler.updateOrderWithDiscount(order, selectedOffers);
                                            try {
                                                displayOrderDetails(superDuperMarket ,order, textPane);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        });

                                        SaleController sl = fxmlLoader.getController();
                                        sl.showSales(superDuperMarket,sales);
                                    } catch (IOException exception) {
                                        throw new RuntimeException(exception);
                                    }

                                }else{
                                    textPane.getChildren().clear();
                                    displayOrderDetails(superDuperMarket, order, textPane);
                                }
                            }
                            catch (Exception e){
                               String msg = e.getMessage();
                            }
                        }
                    });continueButton.setOnAction(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            try {
                                List<Discount> sales = orderManager.checkForSales(superDuperMarket,order);
                                if (sales.size() > 0){
                                    try {
                                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Resources/SaleScreen.fxml"));
                                        Scene scene = new Scene(fxmlLoader.load());
                                        Stage stage = new Stage();
                                        stage.setTitle("Anchor Pane Example");
                                        stage.setScene(scene);
                                        stage.show();
                                        SaleController saleController = fxmlLoader.getController();

                                        saleController.setRefreshOrderCallback(selectedOffers -> {
                                            stage.hide();
                                            orderDetailsHandler.updateOrderWithDiscount(order, selectedOffers);
                                            try {
                                                displayOrderDetails(superDuperMarket ,order, textPane);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        });

                                        SaleController sl = fxmlLoader.getController();
                                        sl.showSales(superDuperMarket,sales);
                                    } catch (IOException exception) {
                                        throw new RuntimeException(exception);
                                    }

                                }else{
                                    textPane.getChildren().clear();
                                    displayOrderDetails(superDuperMarket, order, textPane);
                                }
                            }
                            catch (Exception e){
                               String msg = e.getMessage();
                            }
                        }
                    });
                }
            });
            textPane.getChildren().add(purchaseDate);
            textPane.getChildren().add(orderType);
            textPane.getChildren().add(selectStore);
            textPane.getChildren().add(continueButton);

            int cumulativeHeight = 0;

            CustomerComboBox.setLayoutY(cumulativeHeight);
            cumulativeHeight += CustomerComboBox.getBoundsInLocal().getHeight() + 35;

            purchaseDate.setLayoutY(cumulativeHeight);
            cumulativeHeight += purchaseDate.getBoundsInLocal().getHeight() + 35;

            orderType.setLayoutY(cumulativeHeight);
            cumulativeHeight += orderType.getBoundsInLocal().getHeight() + 35;

            selectStore.setLayoutY(cumulativeHeight);
            cumulativeHeight += selectStore.getBoundsInLocal().getHeight() + 35;

            itemsTable.setLayoutY(cumulativeHeight);
            cumulativeHeight += itemsTable.getBoundsInLocal().getHeight() + 400;

            continueButton.setLayoutY(cumulativeHeight);
            cumulativeHeight += continueButton.getBoundsInLocal().getHeight() + 200;

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void displayOrderDetails(SuperDuperMarket sdm,Order order, Pane textPane) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Resources/OrderDetailsScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Order Details");
        stage.setScene(scene);
        stage.show();

        OrderDetailsController orderDetailsController = fxmlLoader.getController();
        orderDetailsController.showorderDetails(sdm, order, textPane);

       /* FXMLLoader fxmlLoader = new FXMLLoader();

        URL url = getClass().getResource("/Resources/OrderDetailsScreen.fxml");
        fxmlLoader.setLocation(url);
        try {
            textPane = fxmlLoader.load(url.openStream());
        }
        catch (Exception e){

        }*/

    }

    private void BuildFxTableViewItems(List<ItemTable> itemTable) {


        ObservableList data = FXCollections.observableList(itemTable);
        itemsTable.setItems(data);

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn serialCol = new TableColumn("serialNumber");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));

        TableColumn priceCol = new TableColumn("price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn quantityCol = new TableColumn("quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));


        itemsTable.getColumns().setAll(nameCol, serialCol,priceCol,quantityCol);

        itemsTable.setPrefWidth(450);
        itemsTable.setPrefHeight(300);
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Label label = new Label("Items");
        label.setTextFill(Color.DARKBLUE);
        label.setFont(Font.font("Calibri", FontWeight.BOLD, 36));

        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(label);

        // Status message text
        Text actionStatus = new Text();
        actionStatus.setFill(Color.FIREBRICK);

        // Vbox
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(hb, itemsTable, actionStatus);

    }
}