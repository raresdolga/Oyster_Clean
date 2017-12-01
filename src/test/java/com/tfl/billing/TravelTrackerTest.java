package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.billing.Adaptors.CustomerDb;
import com.tfl.billing.Adaptors.OysterCardReaderI;
import com.tfl.billing.Adaptors.PaymentSystemI;
import com.tfl.external.Customer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import utilsForTests.MockSystemClock;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class TravelTrackerTest {

    private JUnitRuleMockery context = new JUnitRuleMockery();
    private MockSystemClock clockTest = new MockSystemClock();

    // test objects for injecting into the travelTracker in order to check the changes
    private List<JourneyEvent> eventLogTest = new ArrayList<>();
    private Set<UUID> currentlyTravellingTest = new HashSet<>();

    private CustomerDb mockDatabase = context.mock(CustomerDb.class);
    private PaymentSystemI paymentSystem = context.mock(PaymentSystemI.class);

    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    TravelTracker travelTracker = new TravelTracker(eventLogTest, currentlyTravellingTest, mockDatabase, paymentSystem);

    @Test
    public void cardScannedCreatesJourneyStart() {
        UUID card_id1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID card_id2 = UUID.fromString("609e72ac-8be3-4476-8b45-01db8c7f122b");
        UUID reader_id1 = UUID.randomUUID();
        UUID reader_id2 = UUID.randomUUID();

        context.checking(new Expectations(){{
            exactly(2).of(mockDatabase).isRegisteredId(card_id1); will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(card_id2); will(returnValue(true));
        }});

        travelTracker.cardScanned(card_id1,reader_id1);
        travelTracker.cardScanned(card_id2,reader_id2);

        assertThat(eventLogTest.size(), is(2));
        assertThat(currentlyTravellingTest.size(), is(2));
        assertThat(eventLogTest.get(0).cardId(), is(card_id1));
        assertThat(eventLogTest.get(1).cardId(), is(card_id2));
    }
    @Test
    public void cardScannedCreatesJourneyEnd() {
        UUID card_id1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID reader_id1 = UUID.randomUUID();
        context.checking(new Expectations(){{
            //oneOf(mockDatabase).getCustomers(); will(returnValue(customerDbAdapter.getCustomers()));
            exactly(1).of(mockDatabase).isRegisteredId(card_id1); will(returnValue(true));
        }});

        travelTracker.cardScanned(card_id1,reader_id1);
        assertThat(currentlyTravellingTest.size(), is(1));
        travelTracker.cardScanned(card_id1,reader_id1);
        assertThat(currentlyTravellingTest.size(), is(0));
        assertThat(eventLogTest.size(), is(2));
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
    }

    @Test
    public void chargeAccountsNone(){
        List<Customer> customers = new ArrayList<>();

        context.checking(new Expectations(){{
            oneOf(mockDatabase).getCustomers(); will(returnValue(customers));
        }});

        travelTracker.chargeAccounts();
    }

    @Test
    public void chargeAccountsTest(){
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Ionut Deaconu", new OysterCard("76800000-8cf0-11bd-b23e-01db8c7f122b")));
        customers.add(new Customer("Andrei Margeloiu", new OysterCard("94619932-8be3-4476-8b45-01db8c7f")));
        customers.add(new Customer("Rares Dolga", new OysterCard("12197779-8be3-4476-8b45-01db8c7f")));

        UUID testReader = UUID.randomUUID();

        clockTest.setCurrentTime(10,20,0);
        JourneyStart journeyStartTest = new JourneyStart(customers.get(0).cardId(), testReader,clockTest);

        clockTest.setCurrentTime(11,20,0);
        JourneyEnd journeyEndTest = new JourneyEnd(customers.get(0).cardId(), testReader,clockTest);

        eventLogTest.add(journeyStartTest);
        eventLogTest.add(journeyEndTest);

        context.checking(new Expectations(){{
            oneOf(mockDatabase).getCustomers(); will(returnValue(customers));
            oneOf(paymentSystem).charge(with(customers.get(0)),with(any(List.class)),with(new BigDecimal(2.40).setScale(2, BigDecimal.ROUND_HALF_UP)));
            oneOf(paymentSystem).charge(customers.get(1), new ArrayList<>(),new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_HALF_UP));
            oneOf(paymentSystem).charge(customers.get(2), new ArrayList<>(),new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP));
        }});

        travelTracker.chargeAccounts();
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
