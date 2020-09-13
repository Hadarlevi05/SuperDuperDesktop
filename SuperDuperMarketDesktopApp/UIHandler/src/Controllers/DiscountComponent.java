package Controllers;

import Enums.OperatorTypeOfSale;
import Handlers.SuperDuperHandler;
import Models.*;
import UIUtils.SelectedItemInDiscount;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;

import java.util.function.Consumer;

public class DiscountComponent extends VBox {

    public Item item;
    private Discount discount;

    private Consumer<SelectedItemInDiscount> selectOfferCallback;

    @FXML private Label lblSale;
    @FXML private Label lblPrice;
    @FXML private Label lblItem;
    @FXML private Label lblQuantity;
    @FXML private VBox vboxOffers;
    @FXML private CheckBox cbxSelectDiscount;

    private SuperDuperHandler superDuperHandler;
    private OperatorTypeOfSale operatorTypeOfSale;

    public DiscountComponent() {
        superDuperHandler = new SuperDuperHandler();
    }

    public void SetDiscount(SuperDuperMarket sdm, Offer offer, Discount discount) {

        this.operatorTypeOfSale = discount.OperatorType;
        this.discount = discount;
        item = superDuperHandler.getItemById(sdm, offer.ItemID);

        lblPrice.setText(Double.toString(offer.ForAdditional) + " ₪");
        lblSale.setText(item.name);
        lblQuantity.setText(Integer.toString((int) offer.Quantity));
        cbxSelectDiscount.selectedProperty().addListener(checkboxChange);
    }

    ChangeListener checkboxChange = new ChangeListener<Boolean>() {

        @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
            if (new_val == true) {

                SelectedItemInDiscount eventData = new SelectedItemInDiscount(item.serialNumber, operatorTypeOfSale, discount.Name, true);

                selectOfferCallback.accept(eventData);
            }
            else{
                SelectedItemInDiscount eventData = new SelectedItemInDiscount(item.serialNumber, operatorTypeOfSale,discount.Name, false);

                selectOfferCallback.accept(eventData);

            }
        }
    };

    public void setSelectionAsChecked() {
        cbxSelectDiscount.setSelected(true);
    }
    public void setSelectionAsUnChecked() {
        cbxSelectDiscount.setSelected(false);
    }
    public void setSelectionAsDisabed() {
        cbxSelectDiscount.setDisable(true);
    }

    public void setCheckboxSelectCallback(Consumer<SelectedItemInDiscount> callback) {
        this.selectOfferCallback = callback ;
    }

    public void setSelectionAsEnabled() {
        cbxSelectDiscount.setDisable(false);

    }
}