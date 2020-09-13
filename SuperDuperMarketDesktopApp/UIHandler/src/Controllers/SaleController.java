package Controllers;

import Enums.OperatorTypeOfSale;
import Models.Discount;
import Models.Offer;
import Models.SuperDuperMarket;
import UIUtils.SelectedItemInDiscount;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SaleController {

    private Consumer<List<Offer>> refreshOrderCallback;
    private List<Discount> discounts;
    @FXML
    private VBox vboxSales = new VBox();
    private List<Button> sales = new ArrayList<>();
    @FXML private AnchorPane salePane = new AnchorPane();
    @FXML private ScrollPane scrolPaneSale = new ScrollPane();
    private Stage primaryStage = new Stage();
    @FXML Label onSaleLabel = new Label();
    @FXML Label lblDiscountOperator = new Label();
    public SaleController(){
        scrolPaneSale.setVvalue(1.0);
    }
    private List<DiscountComponent> childs = new ArrayList<>() ;
    private List<Offer> selectedOffers = new ArrayList<>() ;

    void showSales(SuperDuperMarket sdm, List<Discount> discounts) throws IOException {

        this.discounts = discounts;
        int height = 0;
        for (Discount discount: discounts) {

            onSaleLabel.setText(discount.Name);
            //vboxSales.getChildren().add(onSaleLabel);
            if (discount.OperatorType == OperatorTypeOfSale.ALL_OR_NOTHING){
                lblDiscountOperator.setText("To add all following offers to your order press on any item");
                Button bt = new Button();
                bt.setText("Add");
            }else if(discount.OperatorType == OperatorTypeOfSale.ONE_OF){
                lblDiscountOperator.setText("Please select one of the following offers you would like to add to your order");
            }
            try {
                for (Offer offer:discount.Offers) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Resources/Discount.fxml"));
                    vboxSales.getChildren().add(fxmlLoader.load());
                    DiscountComponent saleComponent = fxmlLoader.getController();

                    childs.add(saleComponent);

                    saleComponent.SetDiscount(sdm,offer, discount);
                    saleComponent.setLayoutY(height * 200);
                    saleComponent.setCheckboxSelectCallback(selectedItemInDiscount -> {
                        updateselectedItemInDiscount(selectedItemInDiscount);

                        if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ONE_OF) {
                            SetOneOf(selectedItemInDiscount.itemID, selectedItemInDiscount.selected);
                        } else if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ALL_OR_NOTHING) {
                            SetAllOrNothing(selectedItemInDiscount.selected);
                        }
                    });
                    height++;
                    //Checkbox tb = (Checkbox)saleComponent.lookup("#cbxSelectDiscount");
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private void updateselectedItemInDiscount(SelectedItemInDiscount selectedItemInDiscount) {
        if (selectedItemInDiscount.selected){
            Discount dis = discounts.stream().filter(disc->disc.Name ==selectedItemInDiscount.discountName).collect(Collectors.toList()).get(0);

            if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ONE_OF){
                Offer slectedOffer =  dis.Offers.stream().filter(offer -> offer.ItemID == selectedItemInDiscount.itemID).collect(Collectors.toList()).get(0);
                List<Offer> offers= new ArrayList<Offer>();
                this.selectedOffers = offers;

            } else if (selectedItemInDiscount.operatorTypeOfSale == OperatorTypeOfSale.ALL_OR_NOTHING){
                this.selectedOffers = dis.Offers;
            }
        }
    }

    public void SetAllOrNothing(boolean selected) {
        if (selected){
            for (DiscountComponent discountComponent: childs) {
                discountComponent.setSelectionAsChecked();
            }
        }
        else{
            for (DiscountComponent discountComponent: childs) {
                discountComponent.setSelectionAsUnChecked();
            }
        }
    }

    public void SetOneOf(int itemId,boolean selected) {
        if (selected){
            for (DiscountComponent discountComponent: childs) {
                if (discountComponent.item.serialNumber != itemId){
                    discountComponent.setSelectionAsDisabed();
                }
            }
        }
        else{
            for (DiscountComponent discountComponent: childs) {
                 discountComponent.setSelectionAsEnabled();
            }
        }
    }

    public void updateOrder(ActionEvent actionEvent) {
        refreshOrderCallback.accept(this.selectedOffers);

    }
    public void setRefreshOrderCallback(Consumer<List<Offer>> callback) {
        this.refreshOrderCallback = callback ;
    }
}
