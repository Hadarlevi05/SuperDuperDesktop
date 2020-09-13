package Controllers;
import Handlers.ItemHandler;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import Models.*;
import UIUtils.CommonUsed;
import UIUtils.StoreItemTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ShowMarketsController {
    @FXML
    private Accordion accodionPane;
    private SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    private ItemHandler itemHandler = new ItemHandler();
    private StoreHandler storeHandler = new StoreHandler();
    void showMarkets(SuperDuperMarket superDuperMarket, Pane textPane) {


/*        TitledPane changedFilesTitledPane = new TitledPane();
        ListView<String> changedFilesListView = new ListView<>();
        TitledPane deletedFilesTitledPane = new TitledPane();
        ListView<String> deletedFilesListView = new ListView<>();*/


        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/ShowMarketsScreen.fxml");
        fxmlLoader.setLocation(url);

        List<Store> stores = superDuperMarket.Stores;
        try {
            accodionPane =  fxmlLoader.load(url.openStream());

            for (int i = 0; i < superDuperMarket.Stores.toArray().length; i++) {
                TitledPane titledPane = new TitledPane();
                GridPane grid = new GridPane();
                TableView<StoreItemTable> tableView = new TableView<StoreItemTable> ();
                ListView<String> listView = new ListView<>();

                ObservableList<String> storeDetails = FXCollections.observableArrayList();

                Store store = stores.get(i);

                storeDetails.add( String.format("Store number %d", i + 1) + ": ");
                storeDetails.add(String.format("Serial number: %d", store.serialNumber));
                storeDetails.add(String.format("Store name: %s", store.name));

                List<OrderItem> orderItems = store.Inventory;
                if (orderItems != null) {
                    List<StoreItemTable> StoreItemTable = new ArrayList<>();

                    for (int j = 0; j < orderItems.toArray().length; j++) {
                        OrderItem oi = orderItems.get(j);
                        Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);
                        if (item!=null){
                            StoreItemTable.add(new StoreItemTable(item.serialNumber,item.name,oi.price,item.purchaseType.toString()));
                        }
                    }

                    BuildFxTableViewItems(StoreItemTable, tableView);

                    //for (int j = 0; j < orderItems.toArray().length; j++) {
                    //  OrderItem orderItem = orderItems.get(j);
                    //Item item = superDuperHandler.getItemById(superDuperMarket, orderItem.itemId);
                    //BuildFxTableViewItems(itemTable);
                    // textPane.getChildren().add(itemsTable);
                    //    storeDetails.add(String.format("%-25s  %-25s  %-25s  %-25s", "Serial number:" + orderItem.itemId, " Name:" + item.name, " Purchase type:" + item.purchaseType.toString(), " Price:" + orderItem.price));
                    //double soldItemsAmount = itemHandler.CalculateSoldItemsAmountPerStore(superDuperMarket, item.serialNumber, store.serialNumber);
                    // storeDetails.add("Amount of sold items: " +String.format("%.2f", soldItemsAmount));
                    // storeDetails.add("");
                    // }
                }
                GetStoreDetails(superDuperMarket, storeDetails, store);

                listView.setItems(storeDetails);

                grid.add(listView,0,0);
                grid.add(tableView,0,1);

                titledPane.setContent(grid);
                titledPane.setText(String.format("Store number %d", i + 1));
                titledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
                accodionPane.getPanes().addAll(titledPane);
            }





/*
            ObservableList<String> changedItems = FXCollections.observableArrayList(changedFiles);
            changedFilesListView.setItems(changedItems);

            ObservableList<String> deletedItems = FXCollections.observableArrayList(deletedFiles);
            deletedFilesListView.setItems(deletedItems);
*/



            //for (int i = 0; i < stores.toArray().length; i++) {
            //    Store store = stores.get(i);
//
            //    System.out.println(String.format("Store number %d", i + 1) + ": ");
            //    System.out.println(String.format("Serial number: %d", store.serialNumber));
            //    System.out.println(String.format("Store name: %s", store.name));
            //    System.out.println("Items:");
//
            //    List<OrderItem> orderItems = store.Inventory;
            //    if (orderItems != null) {
            //        printOrderItems(store.serialNumber, orderItems, true);
            //    }
//
            //    printOrdersOfStore(store);
//
            //    System.out.println(String.format("PPK: %d", store.PPK));
            //    double totalDeliveriesCost = storeHandler.getStoreById(superDuperMarket, store.serialNumber).CalculateTotalDeliveriesCost(superDuperMarket);
            //    System.out.println("Total cost of deliveries from store: " +String.format("%.2f", totalDeliveriesCost) + "\n");
//
            //}





/*            changedFilesTitledPane.setContent(changedFilesListView);
            changedFilesTitledPane.setText("Changed Files:");
            changedFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");

            deletedFilesTitledPane.setContent(deletedFilesListView);
            deletedFilesTitledPane.setText("Deleted Files:");
            deletedFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");*/


            // changedFilesTitledPane, deletedFilesTitledPane

            textPane.getChildren().clear();
            textPane.getChildren().add(accodionPane);
            accodionPane.prefWidthProperty().bind(textPane.widthProperty());

        } catch(IOException e) {
            CommonUsed.showError(e.getMessage());
        }
    }

    private void GetStoreDetails(SuperDuperMarket superDuperMarket, ObservableList<String> storeDetails, Store store) {
        List<Integer> ordersIds = store.OrderHistoryIDs;

        if (ordersIds.size() > 0) {
            storeDetails.add("Orders:");
            for (int orderID : ordersIds) {
                Order order = superDuperMarket.Orders.GetOrderByOrderID(superDuperMarket, orderID);
                DateFormat dateFormat = new SimpleDateFormat("dd/mm-hh:mm");
                storeDetails.add(String.format("%-25s  %-25s  %-25s  %-25s  %-25s", "Date:" +
                        dateFormat.format(order.purchaseDate), "Total amount of items:" +
                        String.format("%.2f", storeHandler.countTotalItemsAmountOfStoreInOrder(order, store)), "Toatal cost of items :" +
                        String.format("%.2f", storeHandler.countTotalCostItemsOfStoreInOrder(order, store)), "Delevery price:" +
                        String.format("%.2f", storeHandler.countDeliveryPriceOfStoreInOrder(order, store)), "Total price:" +
                        String.format("%.2f", storeHandler.countTotalPriceOfStoreInOrder(order, store))));
            }
        }
        storeDetails.add(String.format("PPK: %d", store.PPK));
        double totalDeliveriesCost = storeHandler.getStoreById(superDuperMarket, store.serialNumber).CalculateTotalDeliveriesCost(superDuperMarket);
        storeDetails.add("Total cost of deliveries from store: " +String.format("%.2f", totalDeliveriesCost) + "\n");
    }

    private void BuildFxTableViewItems(List<StoreItemTable> StoreItemTable, TableView<StoreItemTable> tableView) {
        ObservableList data = FXCollections.observableList(StoreItemTable);
        tableView.setItems(data);

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn serialCol = new TableColumn("serialNumber");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));

        TableColumn priceCol = new TableColumn("price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn purchaseTypeCol = new TableColumn("Purchase type");
        purchaseTypeCol.setCellValueFactory(new PropertyValueFactory<>("purchaseType"));


        tableView.getColumns().setAll(nameCol, serialCol,priceCol,purchaseTypeCol);

        tableView.setPrefWidth(450);
        tableView.setPrefHeight(400);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


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
        vbox.getChildren().addAll(hb, tableView, actionStatus);

    }

}