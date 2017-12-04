package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.billing.Adaptors.*;
import com.tfl.external.Customer;

import java.math.BigDecimal;
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
    private PaymentSystemI paymentSystem;
    private final CostCalculator journeyCost;

    public TravelTracker() {
        this.eventLog = new ArrayList<>();
        this.currentlyTravelling = new HashSet<>();
        this.customerDatabase = CustomerDbAdapter.getInstance();
        this.paymentSystem = PaymentSystemAdaptor.getInstance();
        this.journeyCost = new JourneyCostCalculator();
    }

    // dependency injection
    public TravelTracker(List<JourneyEvent> eventLog, Set<UUID> currentlyTravelling, CustomerDb customerDatabase, PaymentSystemI paymentSystem,CostCalculator journeyCost) {
        this.eventLog = eventLog;
        this.currentlyTravelling = currentlyTravelling;
        this.customerDatabase = customerDatabase;
        this.paymentSystem = paymentSystem;
        this.journeyCost = journeyCost;
    }

    // add this travelTracker to listen to changes from the card readers
    public void connect(OysterCardReaderI... cardReaders) {
        for (OysterCardReaderI cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    // called by the card reader to flag that a card was touched
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

    // compute the cost for a client and charge him
    // changed name from totalJourneysFor
    // method refactored
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
