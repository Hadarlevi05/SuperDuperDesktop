package Handlers;

import Models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class OrderDetailsHandler {

    private LocationHandler locationHandler;
    private SuperDuperHandler superDuperHandler;

    public OrderDetailsHandler() {

        locationHandler = new LocationHandler();
        superDuperHandler = new SuperDuperHandler();
    }

    public void updateOrderDetails(SuperDuperMarket sdm, Order order, OrderItem orderItem, Store store, SDMLocation location, Date orderDate, QuantityObject quantityObject) {
        OrderItem orderItemsExist = null;
        if (order.orderItems.size() > 0) {
            List<OrderItem> checkOrder = order.orderItems.stream().filter(x -> x.itemId == orderItem.itemId).collect(Collectors.toList());

            if (checkOrder.size() > 0)
                orderItemsExist = checkOrder.get(0);
        }

        if (orderItemsExist == null) {
            orderItem.quantityObject = quantityObject;
            order.orderItems.add(orderItem);
        } else {
            orderItemsExist.quantityObject.integerQuantity += quantityObject.integerQuantity;
            orderItemsExist.quantityObject.KGQuantity += quantityObject.KGQuantity;
        }

        if (quantityObject.integerQuantity > 0) {
            order.totalItemsNum += quantityObject.integerQuantity;
        }

        if (quantityObject.KGQuantity > 0 && orderItemsExist == null) {
            order.totalItemsNum++;
        }

        if (order.orderItemsFromSales.size()>0){
            for (OrderItem oi: order.orderItemsFromSales) {
                if (oi.quantityObject.integerQuantity > 0) {
                    order.totalItemsNum += quantityObject.integerQuantity;
                }

                if (oi.quantityObject.KGQuantity > 0 && orderItemsExist == null) {
                    order.totalItemsNum++;
                }
            }

        }

        List<OrderItem> allOrderItems =new ArrayList<>();
        allOrderItems.addAll(order.orderItems);
        allOrderItems.addAll(order.orderItemsFromSales);
        order.numOfItemsTypes = allOrderItems.stream().map(x -> x.itemId).collect(Collectors.toList()).size();

        order.totalItemsPrice += quantityObject.integerQuantity > 0 ?
                orderItem.price * quantityObject.integerQuantity :
                orderItem.price * quantityObject.KGQuantity;

        if (order.orderItemsFromSales.size()>0){
            for (OrderItem oi: order.orderItemsFromSales) {
                QuantityObject qo = oi.quantityObject;
                order.totalItemsPrice += qo.integerQuantity > 0 ?
                        orderItem.price * qo.integerQuantity :
                        orderItem.price * qo.KGQuantity;
            }
        }
        order.purchaseDate = orderDate;

        if (order.deliveryPriceByStore.containsKey(store.serialNumber) == false) {
            order.deliveryPriceByStore.put(store.serialNumber, locationHandler.calculateDeliveryCost(store.Location, location, store.PPK));
        }
        if (!order.storesID.contains(store.serialNumber)) {
            order.storesID.add(store.serialNumber);
        }
        order.deliveryPrice = order.deliveryPriceByStore.values().stream().reduce(0.0, Double::sum);
        order.totalPrice = order.totalItemsPrice + order.deliveryPrice;
        order.CustomerLocation = location;

        if (order.orderItemsFromSales.size()>0){
            for (OrderItem oi: order.orderItemsFromSales) {
                order.totalPrice+=oi.price;
            }
        }
    }

    public void updateOrderWithDiscount(SuperDuperMarket sdm, Order order, List<Offer> selectedOffers) {

        List<OrderItem> orderItems = new ArrayList<>();
        for (Offer offer : selectedOffers) {
            OrderItem orderItem = superDuperHandler.getOrderItemById(sdm,offer.ItemID);
            orderItem.isFromSale = true;
            orderItems.add(orderItem);
        }
        order.orderItemsFromSales = orderItems;
    }
}