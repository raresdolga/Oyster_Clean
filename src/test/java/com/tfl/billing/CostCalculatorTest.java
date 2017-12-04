package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.Adaptors.CustomerDb;
import com.tfl.billing.Adaptors.PaymentSystemI;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test;
import utilsForTests.MockSystemClock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CostCalculatorTest {
    private JUnitRuleMockery context = new JUnitRuleMockery();
    private CustomerDb mockDatabase = context.mock(CustomerDb.class);
    private PaymentSystemI paymentSystem = context.mock(PaymentSystemI.class);
    private MockSystemClock clock = new MockSystemClock();
    private List<JourneyEvent> eventLog = new ArrayList<>();

    private CostCalculator costCalculator = new CostCalculator(paymentSystem);

    @Test
    public void testCustomerCostNoJourneys() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Ionut Deaconu", new OysterCard("76800000-8cf0-11bd-b23e-01db8c7f122b")));
        customers.add(new Customer("Andrei Margeloiu", new OysterCard("94619932-8be3-4476-8b45-01db8c7f")));
        customers.add(new Customer("Rares Dolga", new OysterCard("12197779-8be3-4476-8b45-01db8c7f")));
        List<Journey> journeys = new ArrayList<>();

        context.checking(new Expectations() {{
            oneOf(paymentSystem).charge(customers.get(0), journeys, new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP));
            oneOf(paymentSystem).charge(customers.get(1), journeys, new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP));
            oneOf(paymentSystem).charge(customers.get(2), journeys, new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP));
        }});

        for (Customer customer : customers) {
            costCalculator.calculateCost(customer, journeys);
        }
    }

    @Test
    public void testCustomerCostUnfinishedJourney() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Ionut Deaconu", new OysterCard("76800000-8cf0-11bd-b23e-01db8c7f122b")));
        List<Journey> journeys = new ArrayList<>();
        UUID readerId = UUID.randomUUID();

        clock.setCurrentTime(10, 20, 0);
        JourneyStart journeyStart = new JourneyStart(customers.get(0).cardId(), readerId, clock);
        eventLog.add(journeyStart);

        context.checking(new Expectations() {{
            oneOf(mockDatabase).getCustomers();
            will(returnValue(customers));
            oneOf(paymentSystem).charge(customers.get(0), journeys, new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP));
        }});

        for (Customer customer : customers)
            costCalculator.calculateCost(customer, journeys);
    }

    @Test
    public void testCustomerCostFinishedOffPeakJourney() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Ionut Deaconu", new OysterCard("76800000-8cf0-11bd-b23e-01db8c7f122b")));

        OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        OysterCardReader kingsCrossReader = OysterReaderLocator.atStation(Station.KINGS_CROSS);

        clock.setCurrentTime(10, 20, 0);
        JourneyStart journeyStart = new JourneyStart(customers.get(0).cardId(), bakerStreetReader.id(), clock);
        eventLog.add(journeyStart);

        clock.setCurrentTime(11, 20, 0);
        JourneyEnd journeyEnd = new JourneyEnd(customers.get(0).cardId(), kingsCrossReader.id(), clock);
        eventLog.add(journeyEnd);

        List<Journey> journeys = new ArrayList<>();
        journeys.add(new Journey(journeyStart, journeyEnd));

        context.checking(new Expectations() {{
            oneOf(mockDatabase).getCustomers();
            will(returnValue(customers));
            oneOf(paymentSystem).charge(customers.get(0), journeys, new BigDecimal(2.40).setScale(2, BigDecimal.ROUND_HALF_UP));
        }});

        for (Customer customer : customers)
            costCalculator.calculateCost(customer, journeys);
    }

    @Test
    public void testCustomerCostFinishedPeakJourney() {
    }

    @Test
    public void testCustomerCostMixedJourneys() {
    }

    @Test
    public void testCustomerCostMultipleCustomers() {
    }
}
