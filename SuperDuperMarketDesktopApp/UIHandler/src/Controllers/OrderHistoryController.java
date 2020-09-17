package Controllers;

import Handlers.ItemHandler;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import Models.Item;
import Models.Store;
import Models.SuperDuperMarket;
import UIUtils.CommonUsed;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class OrderHistoryController {

    private Accordion accodionPane;
    private SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    private ItemHandler itemHandler = new ItemHandler();
    private StoreHandler storeHandler = new StoreHandler();

    public void showOrderHistory(SuperDuperMarket superDuperMarket, Pane textPane) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/OrderHistoryScreen.fxml");
        fxmlLoader.setLocation(url);

        List<Store> stores = superDuperMarket.Stores;
        try {
            accodionPane =  fxmlLoader.load(url.openStream());

            List<Item> items = superDuperMarket.Items;
            for (int i = 0; i < items.toArray().length; i++) {

                TitledPane newFilesTitledPane = new TitledPane();
                ListView<String> newFilsListView = new ListView<>();

                ObservableList<String> newItems = FXCollections.observableArrayList();

                Item item = items.get(i);
                newItems.add("Serial number: " + item.serialNumber + "\n"
                        + "Name: " + item.name + "\n"
                        + "Purchase type: " + item.purchaseType.toString() + "\n"
                        + "Amount of stores selling this item: " + storeHandler.countSellingStores(superDuperMarket, item.serialNumber) + "\n"
                        + "Average price: " + String.format("%.2f",storeHandler.countAveragePriceOfSellingStores(superDuperMarket, item.serialNumber)) + "\n"
                        + "Amount of sold items: " + String.format("%.2f", itemHandler.CalculateSoldItemsAmount(superDuperMarket ,item.serialNumber)) + "\n");

                newFilsListView.setItems(newItems);

                newFilesTitledPane.setContent(newFilsListView);
                newFilesTitledPane.setText(String.format("Item number %d", i + 1));
                newFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
                accodionPane.getPanes().addAll(newFilesTitledPane);
            }

            textPane.getChildren().clear();
            textPane.getChildren().add(accodionPane);
            accodionPane.prefWidthProperty().bind(textPane.widthProperty());

        } catch(IOException e) {
            CommonUsed.showError(e.getMessage());
        }
    }
}
