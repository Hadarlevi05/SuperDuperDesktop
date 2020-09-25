package Controllers;

import Handlers.CustomerHandler;
import Handlers.ItemHandler;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import Models.Customer;
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
import java.util.ArrayList;
import java.util.List;

public class ShowCustomersController {
    private Accordion accodionPane;
    private SuperDuperHandler superDuperHandler = new SuperDuperHandler();
    private ItemHandler itemHandler = new ItemHandler();
    private StoreHandler storeHandler = new StoreHandler();
    private CustomerHandler customerHandler = new CustomerHandler();

    void showCustomers(SuperDuperMarket superDuperMarket, Pane textPane) {


        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/ShowCustomersScreen.fxml");
        fxmlLoader.setLocation(url);
        List<TitledPane> titledPaneCustomers = new ArrayList<>();
        try {
            accodionPane = fxmlLoader.load(url.openStream());

            List<String> customersDetails = new ArrayList<>();
            int i = 1;
            for (Customer cust : superDuperMarket.Customers) {

                ObservableList<String> newItems = FXCollections.observableArrayList();

                TitledPane newFilesTitledPane = new TitledPane();
                ListView<String> newFilsListView = new ListView<>();
                // id ,name, location, num of orders, avargePrice of orders wuthout delicry, avg prive of delivery

                newItems.add("ID: " + cust.serialNumber);
                newItems.add("Name: " + cust.name);
                newItems.add("Location: " + "[" + cust.location.x + "," + cust.location.y + "]");
                newItems.add("Number of Orders: " + cust.OrderIDs.size());
                newItems.add("Average price of ordered items: " + String.format("%.2f", customerHandler.calculateAvgOfOrderedItems(superDuperMarket, cust)));
                newItems.add("Average price of deliveries: " + String.format("%.2f", customerHandler.calculateAvgOfdeliveries(superDuperMarket, cust)));

                newFilsListView.setItems(newItems);

                newFilesTitledPane.setContent(newFilsListView);
                newFilesTitledPane.setText(String.format("Customer number %d", i));
                i++;
                newFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
                titledPaneCustomers.add(newFilesTitledPane);
            }
            accodionPane.getPanes().addAll(titledPaneCustomers);

            textPane.getChildren().clear();
            textPane.getChildren().add(accodionPane);
            accodionPane.prefWidthProperty().bind(textPane.widthProperty());

        } catch (IOException e) {
            CommonUsed.showError(e.getMessage());
        }
    }
}
