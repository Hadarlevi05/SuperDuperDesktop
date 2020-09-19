package Handlers;

import Models.Customer;
import Models.SDMLocation;
import Models.Store;

import java.util.List;

import static java.lang.Math.sqrt;

public class LocationHandler {

    public double calculateDistanceOfTwoLocations(SDMLocation loc1, SDMLocation loc2) {
        return sqrt((loc2.y - loc2.x) * (loc2.y - loc2.x) + (loc1.y - loc1.x) * (loc1.y - loc1.x));
    }

    public double calculateDeliveryCost(SDMLocation loc1, SDMLocation loc2, int ppk) {
        return ppk * calculateDistanceOfTwoLocations(loc1, loc2);
    }

    public int getMaxXOnMap(List<Store> storeList, List<Customer> customerList) {

        int maxX = 0;

        for (Store store : storeList) {
            maxX = Math.max(maxX, store.Location.x);
        }
        for (Customer customer : customerList) {
            maxX = Math.max(maxX, customer.location.x);
        }
        return maxX;
    }

    public int getMaxYOnMap(List<Store> storeList, List<Customer> customerList) {

        int maxY = 0;

        for (Store store : storeList) {
            maxY = Math.max(maxY, store.Location.y);
        }
        for (Customer customer : customerList) {
            maxY = Math.max(maxY, customer.location.y);
        }
        return maxY;
    }
}
