package UIUtils;

import Enums.OperatorTypeOfSale;

public class SelectedItemInDiscount {

    public SelectedItemInDiscount(int itemID, OperatorTypeOfSale operatorTypeOfSale, String discountName, boolean selected) {
        this.selected = selected;
        this.itemID = itemID;
        this.operatorTypeOfSale = operatorTypeOfSale;
        this.discountName = discountName;
    }
    public boolean selected;
    public int itemID;
    public String discountName;
    public OperatorTypeOfSale operatorTypeOfSale;

}
