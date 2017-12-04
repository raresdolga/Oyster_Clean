package com.tfl.billing.Adaptors;

import com.tfl.billing.Journey;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;


public interface PaymentSystemI {
    void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill);
}
