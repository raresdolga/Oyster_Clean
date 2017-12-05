package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.billing.Adaptors.*;
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
    private final List<JourneyEvent> eventLog;
    private final Set<UUID> currentlyTravelling;
    private final CustomerDatabaseI customerDatabase;
    private final PaymentSystemI paymentSystem;
    private final CostCalculator journeyCost;


    public TravelTracker() {
        this.eventLog = new ArrayList<>();
        this.currentlyTravelling = new HashSet<>();
        this.customerDatabase = new CustomerDatabaseAdapter(CustomerDatabase.getInstance());
        this.paymentSystem = new PaymentSystemAdaptor(PaymentsSystem.getInstance());
        this.journeyCost = new JourneyCostCalculator();
    }

    // DI for mock testing
    public TravelTracker(List<JourneyEvent> eventLog,
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

    // register travelTracker to listen to changes from the OyasterCardReaders
    public void connect(OysterCardReaderI... cardReaders) {
        for (OysterCardReaderI cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        // if the person was registered as travelling, make a JourneyEnd
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            // if the person is not travelling, make a JourneyStart
            if (customerDatabase.isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

    // charge all clients
    public void chargeAccounts() {
        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            chargeCustomer(customer);
        }
    }

    // compute cost for a client and charge him
    private void chargeCustomer(Customer customer) {
        List<Journey> journeys = getJourneys(getCustomerJourneyEvents(customer));

        BigDecimal customerTotal = journeyCost.calculateCustomerTotal(journeys);

        paymentSystem.charge(customer, journeys, customerTotal);
    }

    private List<Journey> getJourneys(List<JourneyEvent> customerJourneyEvents) {
        List<Journey> journeys = new ArrayList<Journey>();

        JourneyEvent start = null;

        for (JourneyEvent event : customerJourneyEvents) {
            // changed from instanceof to boolean method isStart
            if (event.isStart()) {
                start = event;
            } else if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }
        return journeys;
    }

    private List<JourneyEvent> getCustomerJourneyEvents(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<JourneyEvent>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }
        return customerJourneyEvents;
    }
}
