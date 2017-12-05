package com.tfl.billing.Adaptors;

import com.tfl.billing.Journey;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;


/*
    Adapter for PaymentSystem
 */
public class PaymentSystemAdaptor implements PaymentSystemI {
    private final PaymentsSystem paymentsSystem;

    public PaymentSystemAdaptor(PaymentsSystem paymentsSystem) {
        this.paymentsSystem = paymentsSystem;
    }

    @Override
    public void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
        paymentsSystem.charge(customer,journeys,totalBill);
    }
}
