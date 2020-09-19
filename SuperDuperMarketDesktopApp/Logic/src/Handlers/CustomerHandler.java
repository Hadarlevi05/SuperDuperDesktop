package Handlers;

import Models.Customer;
import Models.Store;
import Models.SuperDuperMarket;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerHandler {

    public Customer getCustomerByID(SuperDuperMarket sdm, int serialNumber) {
        List<Customer> customers= sdm.Customers.stream().filter(customer->customer.serialNumber == serialNumber).collect(Collectors.toList());
        if (!customers.isEmpty()){
            return customers.get(0);
        }
        return null;
    }
}
