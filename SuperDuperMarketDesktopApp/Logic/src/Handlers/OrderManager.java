package Handlers;

import Models.*;
import generatedClasses.SuperDuperMarketDescriptor;
import javafx.scene.control.Button;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@XmlRootElement
public class OrderManager {

    public String ordersHistoryPath;
    private  int _orderID = 1;

    @XmlElement
    public Map<Integer, Order> ordersMap = new HashMap<Integer, Order>();

    private StoreHandler storeHandler;

    public OrderManager() {

        storeHandler = new StoreHandler();

    }

    public OrderItem FindCheapestStoreForItem(SuperDuperMarket superDuperMarket, int itemSerialNumber) {

        OrderItem cheapestOrderItem = null;

        for (int i = 0; i < superDuperMarket.Stores.toArray().length; i++){

            Store store = superDuperMarket.Stores.get(i);

            OrderItem orderItem = new StoreHandler().GetOrderItemByItemId(store, itemSerialNumber, true);

            if (cheapestOrderItem == null) {
                cheapestOrderItem = storeHandler.CloneOrderItem(orderItem);

            }
            if (orderItem !=null && cheapestOrderItem!=null){
                if (cheapestOrderItem.price > orderItem.price){
                    cheapestOrderItem = storeHandler.CloneOrderItem(orderItem);
                }
            }
        }
        return cheapestOrderItem;
    }

    public Order GetOrderByOrderID(SuperDuperMarket sdm, int orderId) {
        return sdm.Orders.ordersMap.get(orderId);
    }

    public void addOrder(SuperDuperMarket superDuperMarket, Order order) {

        order.id = _orderID;
        _orderID++;

        for (OrderItem orderItem: order.orderItems) {

            Store store = storeHandler.getStoreById(superDuperMarket, orderItem.storeId);

            if (store.OrderHistoryIDs.contains(order.id) == false) {
                store.OrderHistoryIDs.add(order.id);
            }
        }

        ordersMap.put(order.id, order);
    }

    public List<Discount> checkForSales(SuperDuperMarket superDuperMarket, Order order) {

        List<Discount> sales = new ArrayList<>();
        for (int storeID:order.storesID) {
            Store store = storeHandler.getStoreById(superDuperMarket, storeID);
            for (Discount discount:store.Sales) {
                List<OrderItem> itemsInSale = order.orderItems.stream().filter(item->item.itemId == discount.ItemID).collect(Collectors.toList());
                if (itemsInSale != null && itemsInSale.size()>0){
                    OrderItem itemOnSale = itemsInSale.get(0);
                    double quantity = itemOnSale.quantityObject.integerQuantity > 0 ? itemOnSale.quantityObject.integerQuantity: itemOnSale.quantityObject.KGQuantity;
                    if (quantity >= discount.Quantity){
                        Discount disc = CloneDiscount(discount);
                        disc.numOfOffers = (int)(quantity/discount.Quantity);
                        sales.add(disc);
                    }

                }
            }
        }
        return sales;
    }

    public Discount CloneDiscount(Discount discount) {

        return new Discount(discount.Name,discount.ItemID,discount.Quantity,discount.OperatorType,discount.Offers);
    }


}
