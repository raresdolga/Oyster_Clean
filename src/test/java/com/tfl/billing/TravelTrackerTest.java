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

        context.checking(new Expectations() {{
            exactly(2).of(mockDatabase).isRegisteredId(card_id1);
            will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(card_id2);
            will(returnValue(true));
        }});

        travelTracker.cardScanned(card_id1, reader_id1);
        travelTracker.cardScanned(card_id2, reader_id2);
    }

    @Test
    public void testCardScannedNoJourneys() {
        assertThat(eventLogTest.size(), is(0));
        assertThat(currentlyTravellingTest.size(), is(0));
    }

    @Test
    public void testCardScannedSameReader() {
        UUID cardId1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID readerId = UUID.randomUUID();

        context.checking(new Expectations() {{
            //oneOf(mockDatabase).getCustomers(); will(returnValue(customerDbAdapter.getCustomers()));
            exactly(2).of(mockDatabase).isRegisteredId(cardId1);
            will(returnValue(true));
        }});

        travelTracker.cardScanned(cardId1, readerId);
        travelTracker.cardScanned(cardId1, readerId);

        assertThat(eventLogTest.size(), is(2));
        assertThat(currentlyTravellingTest.size(), is(0));

        assertThat(eventLogTest.get(0).cardId(), is(cardId1));
        assertThat(eventLogTest.get(1).cardId(), is(cardId1));
    }

    @Test
    public void testCardScannedUnfinishedJourneys() {
        UUID cardId1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID cardId2 = UUID.fromString("609e72ac-8be3-4476-8b45-01db8c7f122b");
        UUID readerId1 = UUID.randomUUID();
        UUID readerId2 = UUID.randomUUID();

        context.checking(new Expectations() {{
            //oneOf(mockDatabase).getCustomers(); will(returnValue(customerDbAdapter.getCustomers()));
            exactly(2).of(mockDatabase).isRegisteredId(cardId1);
            will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(cardId2);
            will(returnValue(true));
        }});

        travelTracker.cardScanned(cardId1, readerId1);
        travelTracker.cardScanned(cardId2, readerId2);

        assertThat(eventLogTest.size(), is(2));
        assertThat(currentlyTravellingTest.size(), is(2));

        assertThat(eventLogTest.get(0).cardId(), is(cardId1));
        assertThat(eventLogTest.get(1).cardId(), is(cardId2));
    }

    @Test
    public void testCardScannedFinishedJourney() {
        UUID cardId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID readerId1 = UUID.randomUUID();
        UUID readerId2 = UUID.randomUUID();

        context.checking(new Expectations() {{
            //oneOf(mockDatabase).getCustomers(); will(returnValue(customerDbAdapter.getCustomers()));
            exactly(1).of(mockDatabase).isRegisteredId(cardId);
            will(returnValue(true));
        }});

        travelTracker.cardScanned(cardId, readerId1);
        assertThat(currentlyTravellingTest.size(), is(1));
        assertThat(eventLogTest.size(), is(1));

        travelTracker.cardScanned(cardId, readerId2);
        assertThat(currentlyTravellingTest.size(), is(0));
        assertThat(eventLogTest.size(), is(2));
    }

    @Test
    public void testCardScannedMultipleMixedJourneys() {
        UUID cardId1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID cardId2 = UUID.fromString("609e72ac-8be3-4476-8b45-01db8c7f122b");
        UUID cardId3 = UUID.fromString("07b0bcb1-87df-447f-bf5c-d9961ab9d01e");
        UUID cardId4 = UUID.fromString("3b5a03cb-2be6-4ed3-b83e-94858b43e407");
        UUID cardId5 = UUID.fromString("89adbd1c-4de6-40e5-98bc-b3315c6873f2");
        UUID cardId6 = UUID.fromString("34bbfc54-916b-42b2-a6d8-5ec2eaf7dd7a");
        UUID cardId7 = UUID.fromString("267b3378-592d-4da7-825d-3252982d49ba");
        UUID readerId1 = UUID.randomUUID();
        UUID readerId2 = UUID.randomUUID();
        UUID readerId3 = UUID.randomUUID();

        context.checking(new Expectations() {{
            //oneOf(mockDatabase).getCustomers(); will(returnValue(customerDbAdapter.getCustomers()));
            exactly(1).of(mockDatabase).isRegisteredId(cardId1);
            will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(cardId2);
            will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(cardId3);
            will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(cardId4);
            will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(cardId5);
            will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(cardId6);
            will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(cardId7);
            will(returnValue(true));
        }});

        travelTracker.cardScanned(cardId1, readerId1);
        travelTracker.cardScanned(cardId2, readerId2);
        travelTracker.cardScanned(cardId3, readerId1);
        travelTracker.cardScanned(cardId1, readerId3);

        assertThat(eventLogTest.size(), is(4));
        assertThat(currentlyTravellingTest.size(), is(2));
        assertThat(eventLogTest.get(0).cardId(), is(cardId1));
        assertThat(eventLogTest.get(1).cardId(), is(cardId2));
        assertThat(eventLogTest.get(2).cardId(), is(cardId3));
        assertThat(eventLogTest.get(3).cardId(), is(cardId1));

        travelTracker.cardScanned(cardId4, readerId1);
        travelTracker.cardScanned(cardId2, readerId2);
        travelTracker.cardScanned(cardId5, readerId1);
        travelTracker.cardScanned(cardId6, readerId3);
        travelTracker.cardScanned(cardId7, readerId3);
        travelTracker.cardScanned(cardId7, readerId1);

        assertThat(eventLogTest.size(), is(10));
        assertThat(currentlyTravellingTest.size(), is(4));
        assertThat(eventLogTest.get(4).cardId(), is(cardId4));
        assertThat(eventLogTest.get(5).cardId(), is(cardId2));
        assertThat(eventLogTest.get(6).cardId(), is(cardId5));
        assertThat(eventLogTest.get(7).cardId(), is(cardId6));
        assertThat(eventLogTest.get(8).cardId(), is(cardId7));
        assertThat(eventLogTest.get(9).cardId(), is(cardId7));

        travelTracker.cardScanned(cardId4, readerId2);
        travelTracker.cardScanned(cardId6, readerId2);
        travelTracker.cardScanned(cardId5, readerId1);
        travelTracker.cardScanned(cardId3, readerId1);

        assertThat(eventLogTest.size(), is(14));
        assertThat(currentlyTravellingTest.size(), is(0));
        assertThat(eventLogTest.get(10).cardId(), is(cardId4));
        assertThat(eventLogTest.get(11).cardId(), is(cardId6));
        assertThat(eventLogTest.get(12).cardId(), is(cardId5));
        assertThat(eventLogTest.get(13).cardId(), is(cardId3));
    }

    @Test(expected = UnknownOysterCardException.class)
    public void testCardScannedException() {
        UUID cardId = UUID.randomUUID();
        UUID readerId = UUID.randomUUID();

        context.checking(new Expectations() {{
            oneOf(mockDatabase).isRegisteredId(cardId);
            will(returnValue(false));
        }});

        travelTracker.cardScanned(cardId, readerId);
    }

    @Test
    public void testChargeAccountsNone() {
        List<Customer> customers = new ArrayList<>();

        context.checking(new Expectations(){{
            oneOf(mockDatabase).getCustomers(); will(returnValue(customers));

        }});

        travelTracker.chargeAccounts();
    }

    @Test
    public void chargeAccountsTest() {
        List<Customer> customers = new ArrayList<>();

        customers.add(new Customer("Ionut Deaconu", new OysterCard("76800000-8cf0-11bd-b23e-01db8c7f122b")));
        customers.add(new Customer("Andrei Margeloiu", new OysterCard("94619932-8be3-4476-8b45-01db8c7f")));
        customers.add(new Customer("Rares Dolga", new OysterCard("12197779-8be3-4476-8b45-01db8c7f")));

        UUID testReader = UUID.randomUUID();

        clockTest.setCurrentTime(10,20,0);
        JourneyStart journeyStartTest = new JourneyStart(customers.get(0).cardId(), testReader,clockTest);

        clockTest.setCurrentTime(11,20,0);
        JourneyEnd journeyEndTest = new JourneyEnd(customers.get(0).cardId(), testReader,clockTest);

        List<Journey> testJourneys = new ArrayList<>();

        testJourneys.add(new Journey(journeyStartTest, journeyEndTest));

        eventLogTest.add(journeyStartTest);
        eventLogTest.add(journeyEndTest);

        context.checking(new Expectations(){{
            oneOf(mockDatabase).getCustomers(); will(returnValue(customers));
            oneOf(paymentSystem).charge(with(customers.get(0)),with(any(List.class)),with(new BigDecimal(2.40).setScale(2, BigDecimal.ROUND_HALF_UP)));
            oneOf(paymentSystem).charge(customers.get(1), new ArrayList<>(),new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_HALF_UP));
            oneOf(paymentSystem).charge(customers.get(2), new ArrayList<>(),new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP));
        }});

        travelTracker.chargeAccounts();
        context.checking(new Expectations() {{
            oneOf(paymentSystem).charge(customers.get(0), testJourneys, new BigDecimal(2.40));
            oneOf(paymentSystem).charge(customers.get(1), new ArrayList<>(), new BigDecimal(0));
            oneOf(paymentSystem).charge(customers.get(2), new ArrayList<>(), new BigDecimal(0));
            oneOf(paymentSystem).charge(with(customers.get(0)), with(any(List.class)), with(new BigDecimal(2.40).setScale(2, BigDecimal.ROUND_HALF_UP)));
            oneOf(paymentSystem).charge(customers.get(1), new ArrayList<>(), new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_HALF_UP));
            oneOf(paymentSystem).charge(customers.get(2), new ArrayList<>(), new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP));
        }});
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
