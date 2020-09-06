package Handlers;

import Enums.OperatorTypeOfSale;
import Models.*;
import generatedClasses.*;
import sun.plugin.cache.CacheUpdateHelper;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    SuperDuperMarketDescriptor SDMDescriptor = null;

    public Mapper(SuperDuperMarketDescriptor SDMDescriptor){

        SDMDescriptor = SDMDescriptor;
    }

    public SDMLocation CastLocationToSdmLocation(Location location){
        return new SDMLocation(location.getX(),location.getY());
    }

    public List<Item> CastSDMItemsToListOfItem(List<SDMItem> sdmItems){
        List<Item> items = new ArrayList<Item>();
        for (SDMItem sDMItem : sdmItems) {
            items.add(new Item(sDMItem.getId(), sDMItem.getName(),sDMItem.getPurchaseCategory()));
        }
        return items;
    }

    public List<OrderItem> CastSDMItemsToListOfOrderItemsList(SDMStore sdmStore){
        List<SDMSell> sDMSell  = sdmStore.getSDMPrices().getSDMSell();
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        for (SDMSell sell : sDMSell) {
            orderItems.add(new OrderItem(sell.getItemId(), sell.getPrice(),sdmStore.getId()));
        }
        return orderItems;
    }


    public List<Discount> CastSDMSalesToListOfDiscounts(SDMStore sdmStore){
        SDMDiscounts sDMDiscounts  = sdmStore.getSDMDiscounts();
        if (sDMDiscounts !=null){
            List<Discount> sales = new ArrayList<Discount>();
            for (SDMDiscount sell : sDMDiscounts.getSDMDiscount()) {
                List<Offer> offers = new ArrayList<>();
                for (SDMOffer sdmOffer: sell.getThenYouGet().getSDMOffer()){
                    offers.add(new Offer(sdmOffer.getItemId(),sdmOffer.getQuantity(),sdmOffer.getForAdditional()));
                }
                OperatorTypeOfSale operator = OperatorTypeOfSale.valueOf(sell.getThenYouGet().getOperator().replace("-","_"));

                sales.add(new Discount(sell.getName(), sell.getIfYouBuy().getItemId(),sell.getIfYouBuy().getQuantity(), operator ,offers));
            }
            return sales;
        }
        return null;
    }

    public List<Store> CastSDMStoresToListOfStore(SDMStores sDMStores){
        List<Store> stores = new ArrayList<Store>();
        for (SDMStore sDMStore : sDMStores.getSDMStore()) {
            Store store = new Store(sDMStore.getId(), sDMStore.getName(),sDMStore.getDeliveryPpk(),CastLocationToSdmLocation(sDMStore.getLocation()));
            SDMLocation location = CastLocationToSdmLocation(sDMStore.getLocation());
            stores.add(new Store(sDMStore.getId(), sDMStore.getName(),sDMStore.getDeliveryPpk(),store.Location));
            store.Inventory =CastSDMItemsToListOfOrderItemsList(sDMStore);
        }
    return  stores;

    }

    public List<Customer> CastSDMCustomersToListOfCustomer(SDMCustomers sDMCustomers){
        List<Customer> customers = new ArrayList<Customer>();

        for (SDMCustomer sDMCustomer : sDMCustomers.getSDMCustomer()) {

            Customer customer = new Customer(sDMCustomer.getId(), sDMCustomer.getName(),CastLocationToSdmLocation(sDMCustomer.getLocation()));
            customers.add(customer);
        }
        return  customers;
    }

}
