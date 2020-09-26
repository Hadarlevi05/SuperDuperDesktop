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
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
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
    private static final int LIST_CELL_HEIGHT = 400;
    @FXML
    private Accordion accodionPane;
    private SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    private ItemHandler itemHandler = new ItemHandler();
    private StoreHandler storeHandler = new StoreHandler();
    LocationHandler locationHandler = new LocationHandler();

    void showMarkets(SuperDuperMarket superDuperMarket, Pane textPane) {

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
                ListView<String> listViewOrders = new ListView<>();

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

                listViewOrders.setItems(GetOrdersOfStore(superDuperMarket, store));
                List<Integer> ordersIds = store.OrderHistoryIDs;

                listViewOrders.setMinHeight(150);

                listView.setItems(storeDetails);
                listView.setMinHeight(150);
                tableView.setMinHeight(150);

                //listView.prefHeightProperty().bind(Bindings.size(storeDetails).multiply(LIST_CELL_HEIGHT));
                tableView.setPrefWidth(450);

                //tableView.prefHeightProperty().bind(Bindings.size(storeDetails).multiply(LIST_CELL_HEIGHT));
                tableView.setMaxHeight(Double.MAX_VALUE);
                listView.setMaxHeight(Double.MAX_VALUE);
                listViewOrders.setMaxHeight(Double.MAX_VALUE);


                grid.add(listView, 0, 0);
                grid.add(new Label("Items:"), 0, 1);
                grid.add(tableView, 0, 2);
                if(ordersIds.size() > 0){
                    grid.add(new Label("Orders:"), 0, 3);
                    grid.add(listViewOrders, 0, 4);
                }

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
                grid.setHgap(10); //horizontal gap in pixels => that's what you are asking for
                grid.setVgap(10); //vertical gap in pixels
                grid.setPadding(new Insets(10, 10, 10, 10));
                //grid.setHgap(10); //horizontal gap in pixels
                //grid.setVgap(10);
                newButton.setPadding(new Insets(3, 3, 0, 3));
                grid.add(newButton, 0, 5);

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
                grid.add(deleteButton, 0, 6);


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


                ScrollPane sp = new ScrollPane(grid);
                sp.setFitToWidth(true);
                sp.setFitToHeight(true);

                titledPane.setContent(sp);
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

    private ObservableList<String> GetOrdersOfStore(SuperDuperMarket superDuperMarket, Store store) {
        ObservableList<String> OrderDetails = FXCollections.observableArrayList();

        List<Integer> ordersIds = store.OrderHistoryIDs;

        if (ordersIds.size() > 0) {
            for (int orderID : ordersIds) {
                Order order = superDuperMarket.Orders.GetOrderByOrderID(superDuperMarket, orderID);
                DateFormat dateFormat = new SimpleDateFormat("dd/mm-hh:mm ");
                OrderDetails.add(String.format( "Date: " + dateFormat.format(order.purchaseDate)));
                OrderDetails.add("Total amount of items: " +String.format("%.2f", storeHandler.countTotalItemsAmountOfStoreInOrder(order, store)));
                OrderDetails.add("Toatal cost of items: " + String.format("%.2f", storeHandler.countTotalCostItemsOfStoreInOrder(order, store)));
                OrderDetails.add("Delevery price: " + String.format("%.2f", storeHandler.countDeliveryPriceOfStoreInOrder(order, store)));
                OrderDetails.add("Total price: " + String.format("%.2f", storeHandler.countTotalPriceOfStoreInOrder(order, store)));
            }
        }
        return OrderDetails;
    }

    private TitledPane getTitledPaneForAddStore(SuperDuperMarket superDuperMarket, Pane textPane) {

        SDMLocation storeLoc = new SDMLocation();
        final int[] storeID = new int[1];
        final int[] PPK = new int[1];
        final String[] storeName = new String[1];

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Button addButton = new Button("Add");
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("Store ID "), 0, 0);
        TextField TextFieldID = new TextField();
        Label errorlabelId = new Label("Valid");
        Label errorlabelx = new Label("Valid");
        Label errorlabely = new Label("Valid");
        Label errorlabel = new Label("Valid");

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
        textFieldNameStore.setOnKeyReleased(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                storeName[0] = textFieldNameStore.getText();
            }
        });
        grid.add(textFieldNameStore, 1, 1);
        grid.add(new Label("Location: "), 0, 2);
        grid.add(new Label("X: "), 0, 3);
        TextField TextFieldX = new TextField();

        grid.add(TextFieldX, 1, 3);
        grid.add(new Label("Y: "), 0, 4);
        grid.add(addButton, 0, 8);

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
                int loc = 0;
                try {
                    loc = Integer.parseInt(TextFieldX.getText());
                } catch (Exception e) {
                    errorlabelx.setText("Invalid X");
                    errorlabelx.setStyle("-fx-text-fill:red");
                    errorlabelx.setFont(font);
                    return;
                }
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
        TextFieldPPK.setOnKeyReleased(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                PPK[0] = Integer.parseInt(TextFieldPPK.getText());
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
                int loc = 0;
                try {
                    loc = Integer.parseInt(TextFieldY.getText());
                } catch (Exception e) {
                    errorlabely.setText("Invalid Y");
                    errorlabely.setStyle("-fx-text-fill:red");
                    errorlabely.setFont(font);
                    return;
                }
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

        TitledPane addStoretitledPane = new TitledPane();
        addStoretitledPane.setText("Add New Store");
        addStoretitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
        addStoretitledPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int t = 9;
            }
        });
        List<StoreItemTable> StoreItemTable = new ArrayList<>();
        ListView<StoreItemTable> listView = new ListView<StoreItemTable>();
        ObservableList<StoreItemTable> list = FXCollections.observableArrayList();
        listView.setItems(list);
        ListView<StoreItemTable> selected = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();
            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != listView && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            // if is part of a cell or the cell,
            // handle event instead of using standard handling
            if (node instanceof ListCell) {
                // prevent further handling
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                // focus the listview
                lv.requestFocus();

                if (!cell.isEmpty()) {
                    // handle selection for non-empty cells
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });
        addButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (errorlabelx.getText() == "Valid" && errorlabely.getText() == "Valid" && errorlabelId.getText() == "Valid") {
                    List<StoreItemTable> itemsTable = listView.getSelectionModel().getSelectedItems();
                    if (itemsTable == null || itemsTable.size() == 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The store must sell at least one item\n" +
                                "Please select an item", ButtonType.OK);
                        alert.showAndWait();
                        return;
                    }
                    SDMLocation existLoc = locationHandler.getLocationByXandY(superDuperMarket, storeLoc.x, storeLoc.y);
                    if (existLoc != null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The location of the store you entered is already taken\n" +
                                "Please enter another location", ButtonType.OK);
                        alert.showAndWait();
                        return;
                    }
                    grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 8 && GridPane.getColumnIndex(node) == 1);
                    Store storeToAdd = new Store(storeID[0], storeName[0], PPK[0], storeLoc);

                    List<OrderItem> items = converStoreItemTableToOrderItem(itemsTable, storeToAdd.serialNumber);
                    storeToAdd.Inventory = items;
                    storeHandler.addStore(superDuperMarket, storeToAdd);
                    showMarkets(superDuperMarket, textPane);
                } else {
                    errorlabel.setText("Please fix invalid fields");
                    errorlabel.setStyle("-fx-text-fill:red");
                    errorlabel.setFont(font);
                    grid.add(errorlabel, 1, 8);
                }
            }
        });

        for (Store store : superDuperMarket.Stores) {
            List<OrderItem> orderItems = store.Inventory;


            for (int j = 0; j < orderItems.toArray().length; j++) {
                OrderItem oi = orderItems.get(j);
                Item item = superDuperHandler.getItemById(superDuperMarket, oi.itemId);
                if (item != null) {
                    list.add(new StoreItemTable(oi.price, item));
                }
            }
        }
        grid.add(new Label("Choose items that the store will sell:"), 0, 6);

        grid.add(listView, 0, 7);

        ScrollPane sp = new ScrollPane(grid);
        addStoretitledPane.setContent(sp);

        return addStoretitledPane;
    }

    private List<OrderItem> converStoreItemTableToOrderItem(List<StoreItemTable> itemsTable, int storeID) {
        List<OrderItem> items = new ArrayList<>();
        for (StoreItemTable storeItemTable : itemsTable
        ) {
            items.add(new OrderItem(storeItemTable.serialNumber, storeItemTable.price, storeID));
        }
        return items;
    }

    private int CheckIfStoreExist(SuperDuperMarket superDuperMarket, TextField textFieldID, Label error_label, Font font) {
        int id = 0;
        try {
            id = Integer.parseInt(textFieldID.getText());
        } catch (Exception e) {
            error_label.setText("Invalid ID");
            error_label.setStyle("-fx-text-fill:red");
            error_label.setFont(font);
            return 0;
        }
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

/*        tableView.setPrefWidth(450);
        tableView.setPrefHeight(400);*/
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