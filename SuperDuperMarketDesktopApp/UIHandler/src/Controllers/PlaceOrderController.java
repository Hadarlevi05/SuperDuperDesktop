package Controllers;

import Enums.OrderType;
import Enums.PurchaseType;
import Handlers.OrderDetailsHandler;
import Handlers.OrderManager;
import Handlers.StoreHandler;
import Models.*;
import UIUtils.StoreItemTableOfDynamicOrder;
import UIUtils.StoreItemTableOfStaticOrder;
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
import javafx.scene.layout.*;
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
    private ComboBox<Customer> CustomerComboBox;
    private DatePicker purchaseDate = new DatePicker();
    private ComboBox<OrderType> orderType;
    private ComboBox<Store> selectStore;
    private TableView<StoreItemTableOfStaticOrder> itemsTableStaticOrder = new TableView<StoreItemTableOfStaticOrder>();
    private TableView<StoreItemTableOfDynamicOrder> itemsTableDynamicOrder = new TableView<StoreItemTableOfDynamicOrder>();

    private StoreHandler storeHandler = new StoreHandler();
    private Button continueButton;
    private Button cancelButton;

    private OrderManager orderManager = new OrderManager();
    OrderDetailsHandler orderDetailsHandler = new OrderDetailsHandler();

    void placeOrder(SuperDuperMarket superDuperMarket, Pane textPane) {
        final int[] cumulativeHeight = {5};

        selectStore = new ComboBox<Store>();
        orderType = new ComboBox<OrderType>();
        CustomerComboBox = new ComboBox<Customer>();
        continueButton = new Button("Continue");
        cancelButton = new Button("Cancel");
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
        try {
            accodionPane = fxmlLoader.load(url.openStream());
            ObservableList<Customer> customerDetails = getCustomers(superDuperMarket);
            CustomerComboBox.setItems(customerDetails);
            textPane.getChildren().clear();
            textPane.getChildren().add(CustomerComboBox);
            CustomerComboBox.setLayoutY(cumulativeHeight[0]);

            accodionPane.prefWidthProperty().bind(textPane.widthProperty());
            CustomerComboBox.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    selectedCustomer[0] = CustomerComboBox.getValue();
                    purchaseDate.setDisable(false);
                    cumulativeHeight[0] += CustomerComboBox.getBoundsInLocal().getHeight() + 5;
                    purchaseDate.setLayoutY(cumulativeHeight[0]);
                    textPane.getChildren().add(purchaseDate);

                }
            });
            purchaseDate.setOnAction(event -> {
                orderDate[0] = java.sql.Date.valueOf(purchaseDate.getValue());
                orderType.setDisable(false);
                cumulativeHeight[0] += purchaseDate.getBoundsInLocal().getHeight() + 5;
                orderType.setLayoutY(cumulativeHeight[0]);
                textPane.getChildren().add(orderType);

            });
            orderType.setPromptText("Select order type");
            ObservableList<OrderType> orderTypes = FXCollections.observableArrayList();
            orderTypes.addAll(EnumSet.allOf(OrderType.class));
            orderType.setItems(orderTypes);
            orderType.setOnAction(new EventHandler() {

                @Override
                public void handle(Event event) {
                    OrderType orderTypeSelected = PlaceOrderController.this.orderType.getValue();
                    cumulativeHeight[0] += orderType.getBoundsInLocal().getHeight() + 5;
                    continueButton.setDisable(false);

                    switch (orderTypeSelected) {
                        case STATIC:
                            selectStore.setLayoutY(cumulativeHeight[0]);
                            textPane.getChildren().add(selectStore);
                            selectStore.setDisable(false);
                            ObservableList<Store> storeDetailsForOrder = FXCollections.observableArrayList();
                            for (Store store : superDuperMarket.Stores) {
                                storeDetailsForOrder.add(store);
                            }

                            selectStore.setItems(storeDetailsForOrder);
                            selectStore.setOnAction(new EventHandler() {

                                @Override
                                public void handle(Event event) {

                                    store[0] = selectStore.getValue();
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Shipping cost from the store you selected:" + store[0].PPK, ButtonType.OK);
                                    alert.showAndWait();

                                    List<StoreItemTableOfStaticOrder> itemTable = getItemsForStaticOrder(superDuperMarket, OrderType.STATIC);
                                    BuilDItemsTableOfStaticOrder(itemTable, cumulativeHeight, textPane);
                                    continueButton.setLayoutY(cumulativeHeight[0]);
                                    cancelButton.setLayoutY(cumulativeHeight[0]);
                                    cumulativeHeight[0] += continueButton.getBoundsInLocal().getHeight() + 5;

                                    textPane.getChildren().add(continueButton);
                                    cancelButton.setLayoutX(100);
                                    textPane.getChildren().add(cancelButton);

                                    itemsTableStaticOrder.setOnMouseClicked(new EventHandler() {
                                        @Override
                                        public void handle(Event event) {
                                            StoreItemTableOfStaticOrder selected = itemsTableStaticOrder.getSelectionModel().getSelectedItem();
                                            if (selected.price == null) {
                                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The store you selected does not sell this item", ButtonType.OK);
                                                alert.showAndWait();
                                            } else {
                                                Dialog dialog = new TextInputDialog();
                                                dialog.setHeaderText("Please insert the required quantity");
                                                Optional<String> result = dialog.showAndWait();
                                                if (result.isPresent()) {
                                                    selected.quantity = result.get();
                                                    double parsedQuantity = Double.parseDouble(selected.quantity);
                                                    if (parsedQuantity > 0) {
                                                        itemsTableStaticOrder.refresh();
                                                        OrderItem oi = new OrderItem(selected.serialNumber, selected.price, store[0].serialNumber);
                                                        QuantityObject qo = null;
                                                        if (selected.purchaseType == PurchaseType.QUANTITY) {
                                                            qo = new QuantityObject(Integer.parseInt(selected.quantity), 0);
                                                        } else {
                                                            qo = new QuantityObject(0, parsedQuantity);
                                                        }
                                                        orderDetailsHandler.updateOrderDetails(superDuperMarket, order, oi, store[0], selectedCustomer[0].location, orderDate[0], qo);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            });

                            break;
                        case DYNAMIC:
                            List<StoreItemTableOfDynamicOrder> itemTable = getItemsForDynamicOrder(superDuperMarket, OrderType.DYNAMIC);
                            BuilDItemsTableOfDynamicOrder(itemTable, cumulativeHeight, textPane);
                            continueButton.setLayoutY(cumulativeHeight[0]);
                            cancelButton.setLayoutY(cumulativeHeight[0]);
                            cumulativeHeight[0] += continueButton.getBoundsInLocal().getHeight() + 5;

                            textPane.getChildren().add(continueButton);
                            cancelButton.setLayoutX(100);
                            textPane.getChildren().add(cancelButton);


                            itemsTableDynamicOrder.setOnMouseClicked(new EventHandler() {
                                @Override
                                public void handle(Event event) {
                                    StoreItemTableOfDynamicOrder selected = itemsTableDynamicOrder.getSelectionModel().getSelectedItem();
                                    Dialog dialog = new TextInputDialog();
                                    dialog.setHeaderText("Please insert the required quantity");
                                    Optional<String> result = dialog.showAndWait();
                                    if (result.isPresent()) {
                                        selected.quantity = result.get();
                                        double parsedQuantity = Double.parseDouble(selected.quantity);
                                        if (parsedQuantity > 0) {
                                            itemsTableDynamicOrder.refresh();
                                            OrderItem oi = superDuperMarket.Orders.FindCheapestStoreForItem(superDuperMarket, selected.serialNumber);
                                            Store store = storeHandler.getStoreById(superDuperMarket, oi.storeId);
                                            QuantityObject qo = null;
                                            if (selected.purchaseType == PurchaseType.QUANTITY) {
                                                qo = new QuantityObject(Integer.parseInt(selected.quantity), 0);
                                            } else {
                                                qo = new QuantityObject(0, parsedQuantity);
                                            }
                                            orderDetailsHandler.updateOrderDetails(superDuperMarket, order, oi, store, selectedCustomer[0].location, orderDate[0], qo);
                                        }
                                    }

                                }
                            });

                    }
                    continueButton.setOnAction(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            try {
                                if (order.orderItems.size() == 0) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "No items selected", ButtonType.OK);
                                    alert.showAndWait();
                                } else {
                                    List<Discount> sales = orderManager.checkForSales(superDuperMarket, order);
                                    if (sales.size() > 0) {
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
                                                    displayOrderDetails(superDuperMarket, order, textPane);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            });

                                            SaleController sl = fxmlLoader.getController();
                                            sl.showSales(superDuperMarket, sales);
                                        } catch (IOException exception) {
                                            throw new RuntimeException(exception);
                                        }

                                    } else {
                                        textPane.getChildren().clear();
                                        displayOrderDetails(superDuperMarket, order, textPane);
                                    }
                                }
                            } catch (Exception e) {
                                String msg = e.getMessage();
                            }
                        }
                    });
                    cancelButton.setOnAction(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            textPane.getChildren().clear();
                        }
                    });
                }
            });

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private List<StoreItemTableOfStaticOrder> getItemsForStaticOrder(SuperDuperMarket superDuperMarket, OrderType orderType) {
        List<StoreItemTableOfStaticOrder> itemTable = new ArrayList<>();
        for (Item item : superDuperMarket.Items) {
            OrderItem oi = storeHandler.GetOrderItemByItemId(selectStore.getValue(), item.serialNumber, true);
            if (oi != null) {
                itemTable.add(new StoreItemTableOfStaticOrder(item.serialNumber, item.name, oi.price, item.purchaseType));
            } else {
                itemTable.add(new StoreItemTableOfStaticOrder(item.serialNumber, item.name, null, item.purchaseType));
            }
        }
        return itemTable;
    }

    private List<StoreItemTableOfDynamicOrder> getItemsForDynamicOrder(SuperDuperMarket superDuperMarket, OrderType orderType) {
        List<StoreItemTableOfDynamicOrder> itemTable = new ArrayList<>();

        for (Item item : superDuperMarket.Items) {
            itemTable.add(new StoreItemTableOfDynamicOrder(item.serialNumber, item.name, item.purchaseType));
        }

        return itemTable;
    }

    private ObservableList<Customer> getCustomers(SuperDuperMarket superDuperMarket) {
        ObservableList<Customer> customerDetails = FXCollections.observableArrayList();
        for (Customer customer :
                superDuperMarket.Customers) {
            customerDetails.add(customer);
        }
        return customerDetails;
    }

    private void displayOrderDetails(SuperDuperMarket sdm, Order order, Pane textPane) throws IOException {
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

    private void BuilDItemsTableOfStaticOrder(List<StoreItemTableOfStaticOrder> itemTable, int[] cumulativeHeight, Pane textPane) {


        ObservableList data = FXCollections.observableList(itemTable);
        itemsTableStaticOrder.setItems(data);

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn serialCol = new TableColumn("serialNumber");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));

        TableColumn quantityCol = new TableColumn("quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));


        TableColumn priceCol = new TableColumn("price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        itemsTableStaticOrder.getColumns().setAll(nameCol, serialCol, priceCol, quantityCol);


        itemsTableStaticOrder.setPrefWidth(450);
        itemsTableStaticOrder.setPrefHeight(160);
        itemsTableStaticOrder.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemsTableStaticOrder.scrollTo(0);
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
        vbox.getChildren().addAll(hb, itemsTableStaticOrder, actionStatus);

        StackPane stack_pane = new StackPane(vbox);
        stack_pane.setLayoutY(cumulativeHeight[0]);

        cumulativeHeight[0] += 225;//stack_pane.getBoundsInLocal().getHeight() + 5;
        textPane.getChildren().add(stack_pane);
    }

    private void BuilDItemsTableOfDynamicOrder(List<StoreItemTableOfDynamicOrder> itemTable, int[] cumulativeHeight, Pane textPane) {


        ObservableList data = FXCollections.observableList(itemTable);
        itemsTableDynamicOrder.setItems(data);

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn serialCol = new TableColumn("serialNumber");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));

        TableColumn quantityCol = new TableColumn("quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));


        itemsTableDynamicOrder.getColumns().setAll(nameCol, serialCol, quantityCol);

        itemsTableDynamicOrder.setPrefWidth(450);
        itemsTableDynamicOrder.setPrefHeight(160);
        itemsTableDynamicOrder.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //itemsTable.scrollTo(itemTable.size());
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
        vbox.getChildren().addAll(hb, itemsTableDynamicOrder, actionStatus);

        StackPane stack_pane = new StackPane(vbox);
        stack_pane.setLayoutY(cumulativeHeight[0]);

        cumulativeHeight[0] += 225;//stack_pane.getBoundsInLocal().getHeight() + 5;
        textPane.getChildren().add(stack_pane);


    }
}