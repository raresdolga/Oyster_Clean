package com.tfl.billing.Adaptors;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

/*
    Adapter for database
 */
public class CustomerDatabaseAdapter implements CustomerDatabaseI {
    private CustomerDatabase database;

    public CustomerDatabaseAdapter(CustomerDatabase customerDatabase) {
        this.database = customerDatabase;
    }

    @Override
    public boolean isRegisteredId(UUID cardId) {
        return database.isRegisteredId(cardId);
    }

    @Override
    public List<Customer> getCustomers() {
        return database.getCustomers();
    }
}
