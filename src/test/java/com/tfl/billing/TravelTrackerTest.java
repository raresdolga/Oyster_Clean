package com.tfl.billing;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by Rares on 27.11.2017.
 */
public class TravelTrackerTest {
    TravelTracker travelT = new TravelTracker();
    List<JourneyEvent> journeys;
    JourneyEvent lastAdded;
    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    @Test
    public void cardScannedExceptionTest(){
        UUID cardID_1 = UUID.randomUUID();
        UUID readerID_1 = UUID.randomUUID();
        // test for inexistent cardID
        exceptions.expect(UnknownOysterCardException.class);
        exceptions.expectMessage("Oyster Card does not correspond to a known customer. Id: " + cardID_1);
        travelT.cardScanned(cardID_1,readerID_1);
    }
}
