package Controllers;

import Handlers.CustomerHandler;
import Handlers.LocationHandler;
import Handlers.StoreHandler;
import Models.Customer;
import Models.Store;
import Models.SuperDuperMarket;
import javafx.beans.property.StringPropertyBase;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

import java.util.TooManyListenersException;

public class MapController {

    private Accordion accodionPane;
    private LocationHandler locationHandler;

    GridPane map = new GridPane();

    public MapController() {
        locationHandler = new LocationHandler();
    }

    public void ShowStoresAndOrdersOnMap(SuperDuperMarket superDuperMarket, Pane textPane) {

        int rows = locationHandler.getMaxYOnMap(superDuperMarket.Stores, superDuperMarket.Customers) + 1;
        int columns = locationHandler.getMaxXOnMap(superDuperMarket.Stores, superDuperMarket.Customers) + 1;

        GridPane grid = new GridPane();


        int cc = (int) (textPane.getWidth() - 20) / columns;
        int rc = (int) (textPane.getHeight() - 20) / rows;

        for (Store store : superDuperMarket.Stores) {
            Image image = new Image(getClass().getResource("/Resources/Images/store32.png").toExternalForm());
            //grid.getChildren().add(new ImageView(image));
            ImageView iv = new ImageView(image);
            iv.setId("Store_" + store.serialNumber);
            iv.setFitWidth(cc);
            iv.setFitHeight(rc);
            grid.add(iv, store.Location.x, store.Location.y);
            iv.setOnMouseEntered(new EventHandler<MouseEvent>() {
                                     @Override
                                     public void handle(MouseEvent event) {
                                         handleTooltip(event, superDuperMarket, iv);
                                     }
                                 }
            );

        }

        for (Customer customer : superDuperMarket.Customers) {
            Image image = new Image(getClass().getResource("/Resources/Images/user32.png").toExternalForm());
            //grid.getChildren().add(new ImageView(image));
            ImageView iv = new ImageView(image);
            iv.setId("Customer_" + customer.serialNumber);
            iv.setFitWidth(cc);
            iv.setFitHeight(rc);
            grid.add(iv, customer.location.x, customer.location.y);
            iv.setOnMouseEntered(new EventHandler<MouseEvent>() {
                                     @Override
                                     public void handle(MouseEvent event) {
                                         handleTooltip(event, superDuperMarket, iv);
                                     }
                                 }
            );
        }


        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints(cc);
            grid.getColumnConstraints().add(column);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints(rc);
            grid.getRowConstraints().add(row);
        }
/*
        grid.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                grid.add(Anims.getAnim(1), (int)((me.getSceneX() - (me.getSceneX() % 40)) / 40), (int)((me.getSceneY() - (me.getSceneY() % 40)) / 40)); //here the getAnim argument could be between 1-7
            }
        });*/
        grid.setOnMouseDragOver(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int t = 3;
            }
        });
        grid.setStyle("-fx-background-color: white; -fx-grid-lines-visible: true");


        textPane.getChildren().clear();


        textPane.getChildren().addAll(grid);
    }

    private void handleTooltip(MouseEvent event, SuperDuperMarket superDuperMarket, ImageView iv) {
        Tooltip tp = null;
        String mystring = new String((String) ((ImageView) event.getTarget()).getId());
        if (mystring.contains("Store")) {
            Store store = new StoreHandler().getStoreById(superDuperMarket, Integer.parseInt(mystring.substring(6)));
            tp = new Tooltip("ID: " + store.serialNumber + "\n" + "Name: " + store.name);
        } else {
            Customer customer = new CustomerHandler().getCustomerByID(superDuperMarket, Integer.parseInt(mystring.substring(9)));
            tp = new Tooltip("ID: " + customer.serialNumber + "\n" + "Name: " + customer.name);
        }
        bindTooltip(iv, tp);
    }

    public static void bindTooltip(final Node node, final Tooltip tooltip) {
        node.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // +15 moves the tooltip 15 pixels below the mouse cursor;
                // if you don't change the y coordinate of the tooltip, you
                // will see constant screen flicker
                tooltip.show(node, event.getScreenX(), event.getScreenY() + 15);
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tooltip.hide();
            }
        });
    }
}
