package com.tfl.billing.Adaptors;

import com.tfl.billing.Journey;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Rares on 29.11.2017.
 */
public class PaymentSystemAdaptor implements PaymentSystemI {
    private static PaymentSystemI ourInstance = new PaymentSystemAdaptor();
    private PaymentsSystem paymentsSystem = PaymentsSystem.getInstance();

    public static PaymentSystemI getInstance() {
        return ourInstance;
    }

    private PaymentSystemAdaptor() {
    }

    @Override
    public void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
        paymentsSystem.charge(customer,journeys,totalBill);
    }
}
