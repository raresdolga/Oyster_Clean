package IntegrationTests;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.Adaptors.OysterCardPoll;
import com.tfl.billing.Adaptors.OysterCardReaderI;
import com.tfl.billing.TravelTracker;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;


public class DummyExample {
    public static void main(String[] args) throws Exception {
        OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        OysterCardReaderI paddingtonReader = OysterCardPoll.atStation(Station.PADDINGTON);
        OysterCardReaderI bakerStreetReader = OysterCardPoll.atStation(Station.BAKER_STREET);
        OysterCardReaderI kingsCrossReader = OysterCardPoll.atStation(Station.KINGS_CROSS);

        TravelTracker travelTracker = new TravelTracker();

        travelTracker.connect(paddingtonReader, bakerStreetReader, kingsCrossReader);

        paddingtonReader.touch(myCard);
        minutesPass(0);
        bakerStreetReader.touch(myCard);
        minutesPass(0);
        bakerStreetReader.touch(myCard);
        minutesPass(0);
        kingsCrossReader.touch(myCard);

        travelTracker.chargeAccounts();
    }
    private static void minutesPass(int n) throws InterruptedException {
        Thread.sleep(n * 60 * 1000);
    }
}