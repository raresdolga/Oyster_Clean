package com.tfl.billing.Adaptors;

import com.tfl.external.Customer;

import java.util.List;
import java.util.UUID;


// interface for database so that we code to a behaviour not to a specific implementation + modification for unit Tests
public interface CustomerDb {
    boolean isRegisteredId(UUID cardId);
    List<Customer> getCustomers();
}
