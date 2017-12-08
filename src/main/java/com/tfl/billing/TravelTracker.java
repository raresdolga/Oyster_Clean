package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.billing.adaptors.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.*;

/*
    Track changes in the system and stores the events

    Called to charge the accounts
 */
public class TravelTracker implements ScanListener {
    private final Map<UUID, List<JourneyEvent>> eventLog;
    private final Set<UUID> currentlyTravelling;
    private final CustomerDatabaseI customerDatabase;
    private final PaymentSystemI paymentSystem;
    private final CostCalculator journeyCost;

    // initial public constructor
    public TravelTracker() {
        this(new HashMap<>(),
                new HashSet<>(),
                new CustomerDatabaseAdapter(CustomerDatabase.getInstance()),
                new PaymentSystemAdaptor(PaymentsSystem.getInstance()),
                new JourneyCostCalculator());
    }

    // new constructor with DI
    public TravelTracker(CustomerDatabaseI customerDatabase,
                         PaymentSystemI paymentSystem,
                         CostCalculator journeyCost) {
        this(new HashMap<>(), new HashSet<>(), customerDatabase, paymentSystem, journeyCost);
    }

    // can be used for mock testing
    public TravelTracker(Map<UUID, List<JourneyEvent>> eventLog,
                         Set<UUID> currentlyTravelling,
                         CustomerDatabaseI customerDatabase,
                         PaymentSystemI paymentSystem,
                         CostCalculator journeyCost) {
        this.eventLog = eventLog;
        this.currentlyTravelling = currentlyTravelling;
        this.customerDatabase = customerDatabase;
        this.paymentSystem = paymentSystem;
        this.journeyCost = journeyCost;
    }


    public void connect(OysterCardReaderI... cardReaders) {
        for (OysterCardReaderI cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        // if the person does not have an event
        if (!eventLog.containsKey(cardId)) {
            eventLog.put(cardId, new ArrayList<>());
        }

        // if the person was registered as travelling, make a JourneyEnd
        if (currentlyTravelling.contains(cardId)) {
            eventLog.get(cardId).add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            // if the person is not travelling, make a JourneyStart
            if (customerDatabase.isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.get(cardId).add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

    public void chargeAccounts() {
        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            chargeCustomer(customer);
        }
    }

    private void chargeCustomer(Customer customer) {
        List<Journey> journeys = getJourneys(eventLog.getOrDefault(customer.cardId(), new ArrayList<>()));

        BigDecimal customerTotal = journeyCost.calculateCustomerTotal(journeys);

        paymentSystem.charge(customer, journeys, customerTotal);
    }

    private List<Journey> getJourneys(List<JourneyEvent> customerJourneyEvents) {
        List<Journey> journeys = new ArrayList<Journey>();
        JourneyEvent start = null;

        for (JourneyEvent event : customerJourneyEvents) {
            if (event.isStart()) {
                start = event;
            } else if (start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }

        return journeys;
    }
}
