package Controllers;

import Handlers.ItemHandler;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import Models.Item;
import Models.Order;
import Models.Store;
import Models.SuperDuperMarket;
import UIUtils.CommonUsed;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryController {

    @FXML
    private Accordion accodionPane;
    private SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    private ItemHandler itemHandler = new ItemHandler();
    private StoreHandler storeHandler = new StoreHandler();
    private List<OrderDetailsComponent> childs = new ArrayList<>();
    @FXML
    private VBox orderHistoryVbox = new VBox();
    @FXML
    private ScrollPane orderHistoryScroll;

    public void showOrderHistory(SuperDuperMarket superDuperMarket, Pane textPane) {

        if (!(superDuperMarket.Orders.ordersMap.size() > 0)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No orders made in super duper market yet", ButtonType.OK);
            alert.showAndWait();
            return;

        }

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/OrderHistoryScreen.fxml");
        fxmlLoader.setLocation(url);
        try {
            accodionPane =  fxmlLoader.load(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i = 1;
        List<TitledPane> titledOrderPanes = new ArrayList<>();

        for (Order order : superDuperMarket.Orders.ordersMap.values()) {

            ScrollPane scroll=new ScrollPane();
            scroll.setFitToHeight(true);
            scroll.setFitToWidth(true);
            TitledPane titledPane = new TitledPane();
            titledPane.setContent(scroll);
            scroll.setContent(orderHistoryVbox);
            //newFilesTitledPane.setContent(orderHistoryVbox);
            titledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
            titledPane.setText(String.format("Order number %d", i));
            Accordion accordion = new Accordion();
            List<TitledPane> titledStoresPanes = new ArrayList<>();
            for (int storeID : order.storesID) {
                Store store = storeHandler.getStoreById(superDuperMarket, storeID);

                Node node = null;
                FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/Resources/OrderDetailsPerStore.fxml"));
                try {
                    //orderHistoryVbox.getChildren().add(fxmlLoader2.load());
                    node = fxmlLoader2.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                OrderDetailsComponent orderDetailsComponent = fxmlLoader2.getController();
                orderDetailsComponent.SetOrderDetails(superDuperMarket, order, storeID);
                childs.add(orderDetailsComponent);
                ScrollPane scroll2=new ScrollPane();
                scroll2.setPrefHeight(accodionPane.getHeight());
                scroll2.prefWidth(accodionPane.getWidth());
                TitledPane newFilesTitledPane = new TitledPane();
                scroll2.setContent(node);
                newFilesTitledPane.setContent(scroll2);
                //newFilesTitledPane.setContent(orderHistoryVbox);
                newFilesTitledPane.setText(store.name);
                newFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
                //titledStoresPanes.add(newFilesTitledPane);
                accordion.getPanes().addAll(newFilesTitledPane);

            }
            //accordion.getPanes().addAll(titledStoresPanes);
            accordion.prefWidthProperty().bind(textPane.widthProperty());

            titledPane.setContent(accordion);
            i++;
            titledOrderPanes.add(titledPane);

        }
        accodionPane.prefWidthProperty().bind(textPane.widthProperty());
        accodionPane.getPanes().addAll(titledOrderPanes);
        textPane.getChildren().clear();
        textPane.getChildren().add(accodionPane);


    }
}
