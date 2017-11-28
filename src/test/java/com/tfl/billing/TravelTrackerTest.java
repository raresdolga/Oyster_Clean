package com.tfl.billing;
import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.Adaptors.CustomerDb;
import com.tfl.billing.Adaptors.CustomerDbAdapter;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;

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

    @Test
    public void cardScannedCreatesJourneyStart() {
        OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
        context.checking(new Expectations(){{
            exactly(1).of(mockDatabase).getCustomers(); will(returnValue(customerDbAdapter.getCustomers()));
            exactly(1).of(mockDatabase).isRegisteredId(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d")); will(returnValue(true));
            exactly(1).of(mockDatabase).isRegisteredId(UUID.fromString("609e72ac-8be3-4476-8b45-01db8c7f122b")); will(returnValue(true));
        }});
        TravelTracker travelTracker = new TravelTracker(eventLogTest, currentlyTravellingTest, mockDatabase);
        travelTracker.connect(paddingtonReader);

        paddingtonReader.touch(new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        paddingtonReader.touch(new OysterCard("609e72ac-8be3-4476-8b45-01db8c7f122b"));

        assertThat(eventLogTest.size(), is(2));
        assertThat(currentlyTravellingTest.size(), is(2));
    }

    @Test
    public void cardScannedExceptionTest(){
        UUID cardID_1 = UUID.randomUUID();
        UUID readerID_1 = UUID.randomUUID();

        TravelTracker travelTracker = new TravelTracker(eventLogTest, currentlyTravellingTest, customerDbAdapter);

        // test for inexistent cardID
        exceptions.expect(UnknownOysterCardException.class);
        exceptions.expectMessage("Oyster Card does not correspond to a known customer. Id: " + cardID_1);
        travelTracker.cardScanned(cardID_1,readerID_1);
    }

}
