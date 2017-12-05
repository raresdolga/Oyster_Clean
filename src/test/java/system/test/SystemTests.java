package system.test;

import com.oyster.OysterCard;
import com.tfl.billing.OysterCardAdaptersPoll;
import com.tfl.billing.Adaptors.OysterCardReaderI;
import com.tfl.billing.TravelTracker;
import com.tfl.underground.Station;
import org.junit.Test;


public class SystemTests {
    private static void minutesPass(int n) throws InterruptedException {
        Thread.sleep(n * 60 * 1000);
    }

    @Test
    public void oneClientOnTwoLinesNoWaitingTime() {
        OysterCard client1 = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        OysterCardReaderI paddingtonReader = OysterCardAdaptersPoll.atStation(Station.PADDINGTON);
        OysterCardReaderI bakerStreetReader = OysterCardAdaptersPoll.atStation(Station.BAKER_STREET);
        OysterCardReaderI kingsCrossReader = OysterCardAdaptersPoll.atStation(Station.KINGS_CROSS);

        TravelTracker travelTracker = new TravelTracker();

        travelTracker.connect(paddingtonReader, bakerStreetReader, kingsCrossReader);

        paddingtonReader.touch(client1);
        bakerStreetReader.touch(client1);
        bakerStreetReader.touch(client1);
        kingsCrossReader.touch(client1);

        travelTracker.chargeAccounts();
    }

    @Test
    public void oneClientOnTwoLinesWithWaitingTime() throws InterruptedException {
        OysterCard client1 = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        OysterCardReaderI paddingtonReader = OysterCardAdaptersPoll.atStation(Station.PADDINGTON);
        OysterCardReaderI bakerStreetReader = OysterCardAdaptersPoll.atStation(Station.BAKER_STREET);
        OysterCardReaderI kingsCrossReader = OysterCardAdaptersPoll.atStation(Station.KINGS_CROSS);

        TravelTracker travelTracker = new TravelTracker();

        travelTracker.connect(paddingtonReader, bakerStreetReader, kingsCrossReader);

        paddingtonReader.touch(client1);
        minutesPass(1);
        bakerStreetReader.touch(client1);
        minutesPass(2);
        bakerStreetReader.touch(client1);
        minutesPass(3);
        kingsCrossReader.touch(client1);

        travelTracker.chargeAccounts();
    }

}