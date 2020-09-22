package Controllers;

import Handlers.ItemHandler;
import Handlers.LocationHandler;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import Models.*;
import UIUtils.CommonUsed;

import UIUtils.StoreItemTable;

import UIUtils.StoreItemTableOfStaticOrder;
import generatedClasses.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
import java.util.*;
import java.util.stream.Collectors;

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
            accodionPane = fxmlLoader.load(url.openStream());

            for (int i = 0; i < superDuperMarket.Stores.toArray().length; i++) {
                TitledPane titledPane = new TitledPane();
                GridPane grid = new GridPane();
                TableView<StoreItemTable> tableView = new TableView<StoreItemTable>();
                ListView<String> listView = new ListView<>();

                ObservableList<String> storeDetails = FXCollections.observableArrayList();

                Store store = stores.get(i);

                storeDetails.add(String.format("Store number %d", i + 1) + ": ");
                storeDetails.add(String.format("Serial number: %d", store.serialNumber));
                storeDetails.add(String.format("Store name: %s", store.name));

                List<OrderItem> orderItems = store.Inventory;
                if (orderItems != null) {
                    List<StoreItemTable> StoreItemTable = new ArrayList<>();

                    for (int j = 0; j < orderItems.toArray().length; j++) {
                        OrderItem oi = orderItems.get(j);
                        Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);
                        if (item != null) {
                            StoreItemTable.add(new StoreItemTable(oi.price, item));
                        }
                    }

                    BuildFxTableViewItems(StoreItemTable, tableView);

                }
                GetStoreDetails(superDuperMarket, storeDetails, store);

                listView.setItems(storeDetails);

                grid.add(listView, 0, 0);
                grid.add(tableView, 0, 1);


                Button newButton = new Button();
                newButton.setText("Add New Item");
                newButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ChoiceDialog<Item> dialog = new ChoiceDialog(null, listOfChoicesForDialog(superDuperMarket, store));
                        dialog.setTitle("Add New Item To Store");
                        dialog.setHeaderText("Add New Item To Store");
                        dialog.setContentText("Select item ID:");
                        Optional<Item> result = dialog.showAndWait();

                        if (result.isPresent()) {
                            tableView.setEditable(true);
                            handleAddNewRow(result.get(), store, superDuperMarket, tableView);
                        } else {
                            CommonUsed.showSuccess("There are no new items to add");
                        }
                    }
                });
                grid.setHgap(20); //horizontal gap in pixels 
                grid.setVgap(20);
                newButton.setPadding(new Insets(3, 3, 0, 3));
                grid.add(newButton, 0, 2);

                Button deleteButton = new Button();
                deleteButton.setText("Delete Item");
                deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tableView.setEditable(true);
                        handleDeleteRow(store, superDuperMarket, tableView);

                    }
                });


                deleteButton.setPadding(new Insets(3, 3, 3, 3));
                grid.add(deleteButton, 0, 3);


                tableView.setOnMouseClicked(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        TextInputDialog dialog = new TextInputDialog("0");
                        dialog.setTitle("Edit Price");
                        dialog.setHeaderText("Edit Price of an Item");
                        dialog.setContentText("Please enter new price:");

                        tableView.setEditable(true);
                        StoreItemTable currentItem = tableView.getSelectionModel().getSelectedItem();
                        StoreItemTable storeItemTable = (StoreItemTable) currentItem;
                        OrderItem selected = storeHandler.GetOrderItemByItemId(store, storeItemTable.serialNumber, false);
                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            storeHandler.updateItemPrice(selected, store, superDuperMarket, Double.parseDouble(result.get()));
                            storeItemTable.price = Double.parseDouble(result.get());
                            tableView.refresh();
                            CommonUsed.showSuccess("Price updated successfully");
                        } else
                            CommonUsed.showSuccess("Price wasn't updated successfully");
                    }
                });


                titledPane.setContent(grid);
                titledPane.setText(String.format("Store number %d", i + 1));
                titledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
                accodionPane.getPanes().addAll(titledPane);
            }
            TitledPane addStoretitledPane = getTitledPaneForAddStore(superDuperMarket, textPane);
            accodionPane.getPanes().add(addStoretitledPane);

            textPane.getChildren().clear();
            textPane.getChildren().add(accodionPane);
            accodionPane.prefWidthProperty().bind(textPane.widthProperty());

        } catch (IOException e) {
            CommonUsed.showError(e.getMessage());
        }
    }

    private TitledPane getTitledPaneForAddStore(SuperDuperMarket superDuperMarket, Pane textPane) {

        SDMLocation storeLoc = new SDMLocation();
        final int[] storeID = new int[1];
        final int[] PPK = new int[1];
        final String[] storeName = new String[1];

        GridPane grid = new GridPane();
        grid.setVgap(7);

        Button addButton = new Button("Add");
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("Store ID "), 0, 0);
        TextField TextFieldID = new TextField();
        Label errorlabelId = new Label("Valid");
        Label errorlabelx = new Label("Valid");
        Label errorlabely = new Label("Valid");

        Font font = Font.font("castellar", 16);
        errorlabelId.setFont(font);
        errorlabelx.setFont(font);
        errorlabely.setFont(font);

        TextFieldID.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                storeID[0] = CheckIfStoreExist(superDuperMarket, TextFieldID, errorlabelId, font);
            }

        });
        TextFieldID.setOnKeyReleased(event -> {
            storeID[0] = CheckIfStoreExist(superDuperMarket, TextFieldID, errorlabelId, font);
        });
        grid.add(errorlabelId, 2, 0);
        grid.add(errorlabelx, 2, 3);
        grid.add(errorlabely, 2, 4);

        grid.add(TextFieldID, 1, 0);
        grid.add(new Label("Store Name: "), 0, 1);
        TextField textFieldNameStore = new TextField();
        textFieldNameStore.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                storeName[0] = textFieldNameStore.getText();
            }
        });
        grid.add(textFieldNameStore, 1, 1);
        grid.add(new Label("Location: "), 0, 2);
        grid.add(new Label("X: "), 0, 3);
        TextField TextFieldX = new TextField();

        grid.add(TextFieldX, 1, 3);
        grid.add(new Label("Y: "), 0, 4);
        grid.add(addButton, 0, 6);

        TextFieldX.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int loc = Integer.parseInt(TextFieldX.getText());
                if (loc >= 1 && loc <= 50) {
                    errorlabelx.setStyle("-fx-text-fill:green");
                    errorlabelx.setText("Valid");
                    storeLoc.x = loc;
                } else {
                    errorlabelx.setText("Invalid X");
                    errorlabelx.setStyle("-fx-text-fill:red");
                    errorlabelx.setFont(font);
                }
            }
        });
        TextFieldX.setOnKeyReleased(new EventHandler() {
            @Override
            public void handle(Event event) {
                int loc = Integer.parseInt(TextFieldX.getText());
                if (loc >= 1 && loc <= 50) {
                    errorlabelx.setStyle("-fx-text-fill:green");
                    errorlabelx.setText("Valid");
                    storeLoc.x = loc;
                } else {
                    errorlabelx.setText("Invalid X");
                    errorlabelx.setStyle("-fx-text-fill:red");
                    errorlabelx.setFont(font);
                }
            }
        });

        TextField TextFieldY = new TextField();
        grid.add(TextFieldY, 1, 4);
        grid.add(new Label("PPK: "), 0, 5);
        TextField TextFieldPPK = new TextField();
        TextFieldPPK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PPK[0] = Integer.parseInt(textFieldNameStore.getText());
            }
        });
        grid.add(TextFieldPPK, 1, 5);
        TextFieldY.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int loc = Integer.parseInt(TextFieldY.getText());
                if (loc >= 1 && loc <= 50) {
                    errorlabely.setStyle("-fx-text-fill:green");
                    errorlabely.setText("Valid");
                    storeLoc.y = loc;

                } else {
                    errorlabely.setText("Invalid Y");
                    errorlabely.setStyle("-fx-text-fill:red");
                    errorlabely.setFont(font);
                }
            }
        });
        TextFieldY.setOnKeyReleased(new EventHandler() {
            @Override
            public void handle(Event event) {
                int loc = Integer.parseInt(TextFieldX.getText());
                if (loc >= 1 && loc <= 50) {
                    errorlabely.setStyle("-fx-text-fill:green");
                    errorlabely.setText("Valid");
                    storeLoc.y = loc;
                } else {
                    errorlabely.setText("Invalid X");
                    errorlabely.setStyle("-fx-text-fill:red");
                    errorlabely.setFont(font);
                }
            }
        });

        TitledPane addStoretitledPane = new TitledPane();
        addStoretitledPane.setText("Add New Store");
        addStoretitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
        addStoretitledPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int t = 9;
            }
        });

        addButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Store storeToAdd = new Store(storeID[0], storeName[0], PPK[0], storeLoc);
                storeHandler.addStore(superDuperMarket, storeToAdd);
                showMarkets(superDuperMarket, textPane);
            }
        });
        List<StoreItemTable> StoreItemTable = new ArrayList<>();
        ComboBox<StoreItemTable> itemsComboBox = new ComboBox<>();
        for (Store store:superDuperMarket.Stores) {
            List<OrderItem> orderItems = store.Inventory;


            for (int j = 0; j < orderItems.toArray().length; j++) {
                OrderItem oi = orderItems.get(j);
                Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);
                if (item != null) {
                    StoreItemTable.add(new StoreItemTable(oi.price, item));
                }
            }
        }

        itemsComboBox.getItems().addAll(StoreItemTable);
        grid.add(itemsComboBox,0,6);
        ScrollPane sp = new ScrollPane(grid);
        addStoretitledPane.setContent(sp);

        return addStoretitledPane;
    }


    private int CheckIfStoreExist(SuperDuperMarket superDuperMarket, TextField textFieldID, Label error_label, Font font) {
        int id = Integer.parseInt(textFieldID.getText());
        Store store = storeHandler.getStoreById(superDuperMarket, id);
        if (store == null) {
            error_label.setStyle("-fx-text-fill:green");
            error_label.setText("Valid");
            return id;
        } else {
            error_label.setText("Invalid ID");
            error_label.setStyle("-fx-text-fill:red");
            error_label.setFont(font);
            return 0;
        }
    }

    private void handleDeleteRow(Store store, SuperDuperMarket superDuperMarket, TableView tableView) {
        Object currentItem = tableView.getSelectionModel().getSelectedItem();
        if (currentItem == null) {
            CommonUsed.showSuccess("Item cannot be deleted because no item is selected");
            return;
        }

        StoreItemTable storeItemTable = (StoreItemTable) currentItem;

        boolean success = storeHandler.deleteItemFromStore(storeItemTable.Item, store, superDuperMarket);

        if (success) {
            tableView.getItems().remove(storeItemTable);
            CommonUsed.showSuccess("Item deleted");
        } else {
            CommonUsed.showSuccess("Item cannot be deleted because its sold in 1 store only.");
        }
    }

    private void handleAddNewRow(Item item, Store store, SuperDuperMarket superDuperMarket, TableView tableView) {
        // StoreItemTable storeItemTable = (StoreItemTable)currentItem;
        OrderItem oi = superDuperHandler.getOrderItemById(superDuperMarket, item.serialNumber);
        boolean success = storeHandler.addItemToStore(oi, store, superDuperMarket);
        if (success) {

            // Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);
            if (item != null) {
                tableView.getItems().add(new StoreItemTable(oi.price, item));

                CommonUsed.showSuccess("Item added to store");
            } else {
                CommonUsed.showSuccess("Item cannot be added to store.");
            }
        }
    }

    private List<Item> listOfChoicesForDialog(SuperDuperMarket superDuperMarket, Store store) {
        List<Item> choices = superDuperMarket.Items.stream().collect(Collectors.toList());
        choices.removeAll(store.Inventory.stream().map(x -> superDuperHandler.getItemById(superDuperMarket, x.itemId)).collect(Collectors.toList()));

        return choices;
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
        storeDetails.add("Total cost of deliveries from store: " + String.format("%.2f", totalDeliveriesCost) + "\n");
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


        tableView.getColumns().setAll(nameCol, serialCol, priceCol, purchaseTypeCol);

        tableView.setPrefWidth(450);
        tableView.setPrefHeight(400);
        tableView.autosize();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Label label = new Label("Items");
        //label.setTextFill(Color.DARKBLUE);
        label.setFont(Font.font("Calibri", FontWeight.BOLD, 36));

        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(label);

        // Status message text
        Text actionStatus = new Text();
        //actionStatus.setFill(Color.FIREBRICK);

        // Vbox
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(hb, tableView, actionStatus);

    }

}