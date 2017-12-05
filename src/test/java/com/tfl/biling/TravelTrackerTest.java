package com.tfl.biling;

import com.oyster.OysterCard;
import com.tfl.biling.utils.MockSystemClock;
import com.tfl.billing.*;
import com.tfl.billing.Adaptors.CustomerDatabaseI;
import com.tfl.billing.Adaptors.OysterCardReaderI;
import com.tfl.billing.Adaptors.PaymentSystemI;
import com.tfl.external.Customer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class TravelTrackerTest {

    private final JUnitRuleMockery context = new JUnitRuleMockery();
    private final MockSystemClock clockTest = new MockSystemClock();

    // mock objects for injecting into the travelTracker, to check the changes
    private final List<JourneyEvent> eventLogTest = new ArrayList<>();
    private final Set<UUID> currentlyTravellingTest = new HashSet<>();
    private final CustomerDatabaseI mockDatabase = context.mock(CustomerDatabaseI.class);
    private final PaymentSystemI paymentSystem = context.mock(PaymentSystemI.class);
    private final CostCalculator journeyCost = context.mock(CostCalculator.class);

    @Rule
    public final ExpectedException exceptions = ExpectedException.none();

    private final TravelTracker travelTracker = new TravelTracker(eventLogTest, currentlyTravellingTest, mockDatabase, paymentSystem,journeyCost);

    @Test
    public void cardScannedCreatesJourneyStart() {
        UUID card_id1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID card_id2 = UUID.fromString("609e72ac-8be3-4476-8b45-01db8c7f122b");
        UUID reader_id1 = UUID.randomUUID();
        UUID reader_id2 = UUID.randomUUID();

        context.checking(new Expectations(){{
            oneOf(mockDatabase).isRegisteredId(card_id1); will(returnValue(true));
            oneOf(mockDatabase).isRegisteredId(card_id2); will(returnValue(true));
        }});

        travelTracker.cardScanned(card_id1,reader_id1);
        travelTracker.cardScanned(card_id2,reader_id2);

        assertThat(eventLogTest.size(), is(2));
        assertThat(currentlyTravellingTest.size(), is(2));
        assertThat(eventLogTest.get(0).cardId(), is(card_id1));
        assertThat(eventLogTest.get(1).cardId(), is(card_id2));

        context.assertIsSatisfied();
    }

    @Test
    public void cardScannedCreatesJourneyEnd() {
        UUID card_id1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID reader_id1 = UUID.randomUUID();
        context.checking(new Expectations(){{
            oneOf(mockDatabase).isRegisteredId(card_id1); will(returnValue(true));
        }});

        travelTracker.cardScanned(card_id1,reader_id1);
        assertThat(currentlyTravellingTest.size(), is(1));
        travelTracker.cardScanned(card_id1,reader_id1);
        assertThat(currentlyTravellingTest.size(), is(0));
        assertThat(eventLogTest.size(), is(2));

        context.assertIsSatisfied();
    }

    @Test
    public void cardScannedExceptionTest(){
        UUID cardID_1 = UUID.randomUUID();
        UUID readerID_1 = UUID.randomUUID();

        context.checking(new Expectations(){{
            oneOf(mockDatabase).isRegisteredId(cardID_1); will(returnValue(false));
        }});

        // test for inexistent cardID
        exceptions.expect(UnknownOysterCardException.class);
        exceptions.expectMessage("Oyster Card does not correspond to a known customer. Id: " + cardID_1);

        travelTracker.cardScanned(cardID_1,readerID_1);

        context.assertIsSatisfied();
    }

    @Test
    public void chargeAccountsNone(){
        List<Customer> customers = new ArrayList<>();

        context.checking(new Expectations(){{
            oneOf(mockDatabase).getCustomers(); will(returnValue(customers));
        }});

        travelTracker.chargeAccounts();

        context.assertIsSatisfied();
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    public void chargeAccountsTest(){
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Mark Anton", new OysterCard("76800000-8cf0-11bd-b23e-01db8c7f122b")));
        customers.add(new Customer("Jessica Anton", new OysterCard("94619932-8be3-4476-8b45-01db8c7f")));

        UUID testReader = UUID.randomUUID();

        clockTest.setCurrentTime(10,20,0);
        JourneyStart journeyStartTest = new JourneyStart(customers.get(0).cardId(), testReader, clockTest);

        clockTest.setCurrentTime(11,20,0);
        JourneyEnd journeyEndTest = new JourneyEnd(customers.get(0).cardId(), testReader, clockTest);

        eventLogTest.add(journeyStartTest);
        eventLogTest.add(journeyEndTest);

        BigDecimal totalCost1 = new BigDecimal(2.40).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalCost2 = new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);

        context.checking(new Expectations(){{
            oneOf(mockDatabase).getCustomers(); will(returnValue(customers));
            oneOf(journeyCost).calculateCustomerTotal(with(any(List.class))); will(returnValue(totalCost1));
            oneOf(paymentSystem).charge(with(customers.get(0)),with(any(List.class)),with(totalCost1));
            oneOf(journeyCost).calculateCustomerTotal(new ArrayList<Journey>()); will(returnValue(totalCost2));
            oneOf(paymentSystem).charge(customers.get(1), new ArrayList<>(),totalCost2);
        }});

        travelTracker.chargeAccounts();

        context.assertIsSatisfied();
    }

    @Test
    public void connectTest(){
        OysterCardReaderI station1 = context.mock(OysterCardReaderI.class,"station1");
        OysterCardReaderI station2 = context.mock(OysterCardReaderI.class,"station2");
        OysterCardReaderI station3 = context.mock(OysterCardReaderI.class,"station3");

        context.checking(new Expectations(){{
            exactly(1).of(station1).register(travelTracker);
            exactly(1).of(station2).register(travelTracker);
            exactly(1).of(station3).register(travelTracker);
        }});

        travelTracker.connect(station1,station2,station3);
    }
}
