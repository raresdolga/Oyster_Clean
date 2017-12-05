package com.tfl.billing.adaptors;

import com.tfl.external.Customer;

import java.util.List;
import java.util.UUID;


/*
    Interface for implementing Adapter Pattern
 */
public interface CustomerDatabaseI {
    boolean isRegisteredId(UUID cardId);
    List<Customer> getCustomers();
}
