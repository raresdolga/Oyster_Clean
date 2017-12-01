package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.billing.Adaptors.*;
import com.tfl.external.Customer;

import java.util.*;

/*
    Tracks some changes in the system and stores the events
 */
public class TravelTracker implements ScanListener {

    // all events that happened in the system
    private final List<JourneyEvent> eventLog;
    //  packagers that are currently travelling
    private final Set<UUID> currentlyTravelling;
    // the database in use
    private final CustomerDb customerDatabase;
    private final PaymentSystemI paymentSystem;
    private final CostCalculator costCalculator;

    public TravelTracker() {
        this.eventLog = new ArrayList<JourneyEvent>();
        this.currentlyTravelling = new HashSet<UUID>();
        this.customerDatabase = CustomerDbAdapter.getInstance();
        this.paymentSystem = PaymentSystemAdaptor.getInstance();
        this.costCalculator = new CostCalculator(paymentSystem);
    }

    // dependency injection => reduces dependency, makes the code more reusable and testable
    public TravelTracker(List<JourneyEvent> eventLog, Set<UUID> currentlyTravelling, CustomerDb customerDatabase, PaymentSystemI paymentSystem) {
        this.eventLog = eventLog;
        this.currentlyTravelling = currentlyTravelling;
        this.customerDatabase = customerDatabase;
        this.paymentSystem = paymentSystem;
        this.costCalculator = new CostCalculator(paymentSystem);
    }

    public void chargeAccounts() {
        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            totalJourneysFor(customer);
        }
    }

    private void totalJourneysFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<JourneyEvent>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }

        List<Journey> journeys = new ArrayList<Journey>();

        JourneyEvent start = null;

        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }

        costCalculator.calculateCost(customer, journeys);
    }

    public void connect(OysterCardReaderI... cardReaders) {
        for (OysterCardReaderI cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            if (CustomerDbAdapter.getInstance().isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }
}
