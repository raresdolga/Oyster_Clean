package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.billing.Adaptors.CustomerDb;
import com.tfl.billing.Adaptors.CustomerDbAdapter;
import com.tfl.external.Customer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class TravelTrackerTest {
    private JUnitRuleMockery context = new JUnitRuleMockery();

    // test objects for injecting into the travelTracker in order to check the changes
    private List<JourneyEvent> eventLogTest = new ArrayList<>();
    private Set<UUID> currentlyTravellingTest = new HashSet<>();
    CustomerDb mockDatabase = context.mock(CustomerDb.class);
    CustomerDb customerDbAdapter = CustomerDbAdapter.getInstance();

    @Rule
    public ExpectedException exceptions = ExpectedException.none();
    TravelTracker travelTracker = new TravelTracker(eventLogTest, currentlyTravellingTest, mockDatabase);

    @Test
    public void cardScannedCreatesJourneyStart() {
        UUID card_id1 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID card_id2 = UUID.fromString("609e72ac-8be3-4476-8b45-01db8c7f122b");
        UUID reader_id1 = UUID.randomUUID();
        UUID reader_id2 = UUID.randomUUID();
        context.checking(new Expectations(){{
            //oneOf(mockDatabase).getCustomers(); will(returnValue(customerDbAdapter.getCustomers()));
            exactly(2).of(mockDatabase).isRegisteredId(card_id1); will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(card_id2); will(returnValue(true));
        }});

        /*
        Need to test just the card scanned functionality so do not involve other methods
            travelTracker.connect(paddingtonReader);
            paddingtonReader.touch(new OysterCard(read_id1));
            paddingtonReader.touch(new OysterCard(read_id2));
       */
        travelTracker.cardScanned(card_id1,reader_id1);
        travelTracker.cardScanned(card_id2,reader_id2);
        assertThat(eventLogTest.size(), is(2));
        assertThat(currentlyTravellingTest.size(), is(2));
        assertThat(eventLogTest.get(0).cardId(), is(card_id1));
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
    public void chargeAccounts(){
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Ionut Deaconu", new OysterCard("76800000-8cf0-11bd-b23e-01db8c7f122b")));
        customers.add(new Customer("Andrei Margeloiu", new OysterCard("94619932-8be3-4476-8b45-01db8c7f")));
        customers.add(new Customer("Rares Dolga", new OysterCard("12197779-8be3-4476-8b45-01db8c7f")));
        context.checking(new Expectations(){{
            oneOf(mockDatabase).getCustomers(); will(returnValue(customers));
        }});
        travelTracker.chargeAccounts();
    }
}
