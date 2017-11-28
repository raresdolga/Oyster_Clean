package com.tfl.billing.Adaptors;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

/**
 * Created by Rares on 25.11.2017.
 */
public class CustomerDbAdapter implements CustomerDb {
    private static CustomerDb ourInstance = new CustomerDbAdapter();
    private CustomerDatabase db = CustomerDatabase.getInstance();

    public static CustomerDb getInstance() {
        return ourInstance;
    }

    private CustomerDbAdapter() {
    }

    @Override
    public boolean isRegisteredId(UUID cardId) {
        return db.isRegisteredId(cardId);
    }

    @Override
    public List<Customer> getCustomers() {
        return db.getCustomers();
    }
}
