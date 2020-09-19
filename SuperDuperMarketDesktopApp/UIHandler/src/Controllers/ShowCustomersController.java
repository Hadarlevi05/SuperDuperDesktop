package Controllers;

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
    void showCustomers(SuperDuperMarket superDuperMarket, Pane textPane) {


        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/ShowCustomersScreen.fxml");
        fxmlLoader.setLocation(url);
        List<TitledPane> titledPaneCustomers = new ArrayList<>();
        try {
            accodionPane =  fxmlLoader.load(url.openStream());

            List<Customer> customers = superDuperMarket.Customers;
            for (int i = 0; i < customers.toArray().length; i++) {
                ObservableList<Customer> newItems = FXCollections.observableArrayList();

                TitledPane newFilesTitledPane = new TitledPane();
                ListView<Customer> newFilsListView = new ListView<>();

                Customer customer = customers.get(i);

                newItems.add(customer);
                newFilsListView.setItems(newItems);

                newFilesTitledPane.setContent(newFilsListView);
                newFilesTitledPane.setText(String.format("Customer number %d", i + 1));
                newFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");
                titledPaneCustomers.add(newFilesTitledPane);
            }
            accodionPane.getPanes().addAll(titledPaneCustomers);

            textPane.getChildren().clear();
            textPane.getChildren().add(accodionPane);
            accodionPane.prefWidthProperty().bind(textPane.widthProperty());

        } catch(IOException e) {
            CommonUsed.showError(e.getMessage());
        }
    }
}
