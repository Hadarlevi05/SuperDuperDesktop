package Models;

import Enums.OperatorTypeOfSale;

import java.util.List;

public class Discount {

    public Discount(String Name, int ItemID,double Quantity,OperatorTypeOfSale OperatorType,List<Offer> offers){
        this.Name = Name;
        this.ItemID = ItemID;
        this.Quantity = Quantity;
        this.OperatorType = OperatorType;
        this.Offers = offers;
    }

    public String Name;
    public int ItemID;
    public double Quantity;
    public int Id;
    public OperatorTypeOfSale OperatorType;
    public List<Offer> Offers;

}