package Controllers;

import Enums.OperatorTypeOfSale;
import Models.Discount;
import Models.Offer;
import Models.Order;
import Models.SuperDuperMarket;
import UIUtils.SelectedItemInDiscount;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SaleController {

    private Consumer<List<Offer>> refreshOrderCallback;
    private List<Discount> discounts;
    @FXML
    private VBox vboxSales = new VBox();
    private List<Button> sales = new ArrayList<>();
    @FXML
    private AnchorPane salePane = new AnchorPane();
    @FXML
    private ScrollPane scrolPaneSale;
    private Stage primaryStage = new Stage();
    @FXML
    Label onSaleLabel = new Label();
    @FXML
    Label lblDiscountOperator = new Label();
    /*    public SaleController(){
            scrolPaneSale.setVvalue(1.0);
        }*/
    public Map<Integer, List<DiscountComponent>> childs = new HashMap<>();

    private List<Offer> selectedOffers = new ArrayList<>();

    void showSales(SuperDuperMarket sdm, List<Discount> discounts) throws IOException {

        GridPane grid = new GridPane();
        scrolPaneSale.setVvalue(1.0);
        scrolPaneSale.setFitToWidth(true);
        scrolPaneSale.setFitToHeight(true);

        this.discounts = discounts;
        int height = 0;
        for (Discount discount : discounts) {
            childs.put(discount.Id,new ArrayList<>());

            Label onSaleLabel = new Label(discount.Name);
            vboxSales.getChildren().add(onSaleLabel);
            onSaleLabel.setStyle(" -fx-text-fill: #ff0707; -fx-text-Alignment: CENTER; -fx-font-size: 20");

            Label chooseItem = new Label();
            if (discount.OperatorType == OperatorTypeOfSale.ALL_OR_NOTHING) {
                chooseItem = new Label("To add all following offers to your order press on any item");


            } else if (discount.OperatorType == OperatorTypeOfSale.ONE_OF) {
                chooseItem = new Label("Please select one of the following offers you would like to add to your order");
            }
            chooseItem.setStyle("-fx-text-fill: #ff0707; -fx-text-Alignment: CENTER; -fx-font-size: 20");
            vboxSales.getChildren().add(chooseItem);

            try {
                for (Offer offer : discount.Offers) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Resources/Discount.fxml"));
                    vboxSales.getChildren().add(fxmlLoader.load());
                    DiscountComponent saleComponent = fxmlLoader.getController();

                    childs.get(discount.Id).add(saleComponent);
                    vboxSales.getChildren().add(saleComponent);

                    saleComponent.SetDiscount(sdm, offer, discount);
                    saleComponent.setLayoutY(height * 200);
                    saleComponent.setCheckboxSelectCallback(selectedItemInDiscount -> {
                        updateselectedItemInDiscount(selectedItemInDiscount);

                        if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ONE_OF) {
                            SetOneOf(selectedItemInDiscount);
                        } else if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ALL_OR_NOTHING) {
                            SetAllOrNothing(selectedItemInDiscount, selectedItemInDiscount.selected);
                        }
                    });
                    height++;

                    //Checkbox tb = (Checkbox)saleComponent.lookup("#cbxSelectDiscount");
                }
                scrolPaneSale.setContent(vboxSales);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        Button continueButton = new Button("Continue");
        continueButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateOrder(event);
            }
        });
        vboxSales.getChildren().add(continueButton);
        //    }
    }

    private void updateselectedItemInDiscount(SelectedItemInDiscount selectedItemInDiscount) {
        if (selectedItemInDiscount.selected) {
            Discount dis = discounts.stream().filter(disc -> disc.Name == selectedItemInDiscount.discountName && selectedItemInDiscount.InstanceId == disc.Id).collect(Collectors.toList()).get(0);

            if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ONE_OF) {
                Offer slectedOffer = dis.Offers.stream().filter(offer -> offer.ItemID == selectedItemInDiscount.itemID).collect(Collectors.toList()).get(0);
                this.selectedOffers.add(slectedOffer);

            } else if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ALL_OR_NOTHING) {
                this.selectedOffers.addAll(dis.Offers);
            }
        }
    }

    public void SetAllOrNothing(SelectedItemInDiscount selectedItemInDiscount, boolean selected) {
        if (selected) {
            for (DiscountComponent discountComponent : childs.get(selectedItemInDiscount.InstanceId)) {
                if (discountComponent.item.serialNumber != selectedItemInDiscount.itemID &&
                        discountComponent.discount.Id == selectedItemInDiscount.InstanceId) {
                    discountComponent.setSelectionAsChecked();
                }
            }
        } else {
            for (DiscountComponent discountComponent : childs.get(selectedItemInDiscount.InstanceId)) {
                if (discountComponent.discount.Id == selectedItemInDiscount.InstanceId) {
                    discountComponent.setSelectionAsUnChecked();
                }
            }
        }
    }

    public void SetOneOf(SelectedItemInDiscount selectedItemInDiscount) {
        if (selectedItemInDiscount.selected) {
            for (DiscountComponent discountComponent : childs.get(selectedItemInDiscount.InstanceId)) {
                if (discountComponent.item.serialNumber != selectedItemInDiscount.itemID &&
                        discountComponent.discount.Id == selectedItemInDiscount.InstanceId) {
                    discountComponent.setSelectionAsDisabed();
                }
            }
        } else {
            for (DiscountComponent discountComponent : childs.get(selectedItemInDiscount.InstanceId)) {
                if (discountComponent.discount.Id == selectedItemInDiscount.InstanceId) {
                    discountComponent.setSelectionAsEnabled();
                }
            }
        }
    }

    public void updateOrder(ActionEvent actionEvent) {
/*        List<DiscountComponent> offers = childs.stream().filter(x -> x.cbxSelectDiscount.isSelected()).collect(Collectors.toList());
        for (DiscountComponent o : offers) {
            int q = (int) o.discount.Offers.stream().filter(x -> x.ItemID == o.item.serialNumber).findFirst().get().Quantity;

            Offer offerExists = selectedOffers.stream().filter(x -> x.ItemID == o.item.serialNumber).findFirst().orElse(null);
            if (offerExists == null) {
                this.selectedOffers.add(new Offer(o.item.serialNumber, q, offerExists.ForAdditional));
            } else {
                offerExists.Quantity += q;
            }
        }*/

        refreshOrderCallback.accept(this.selectedOffers);

    }

    public void setRefreshOrderCallback(Consumer<List<Offer>> callback) {
        this.refreshOrderCallback = callback;
    }
}
