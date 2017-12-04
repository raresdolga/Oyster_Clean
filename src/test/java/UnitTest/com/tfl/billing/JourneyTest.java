package UnitTest.com.tfl.billing;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyStart;
import org.junit.Before;
import org.junit.Test;
import UnitTest.com.tfl.billing.utilsUnitTest.MockSystemClock;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Rares on 27.11.2017.
 */
public class JourneyTest {

    MockSystemClock controlClock;
    JourneyStart jS;
    JourneyEnd jE;
    private Journey journey;
    String dateVars;
    // must initialize all variables before

    @Before
    public void setVars() {

        controlClock = new MockSystemClock();

        controlClock.setCurrentTime(20, 0, 23);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);

        controlClock.setCurrentTime(21,22,0);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);

        journey = new Journey(jS,jE);
        dateVars = controlClock.getyearMonthDay();
    }

    @Test
    public void durationSecondsTest(){

        controlClock.setCurrentTime(20, 0, 23);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);

        controlClock.setCurrentTime(22,22,0);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);

        assertThat(journey.durationSeconds(), is(8497));

    }

    @Test
    public void durationMinutesTest(){

        controlClock.setCurrentTime(20, 0, 23);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);

        controlClock.setCurrentTime(21,22,00);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);

        journey = new Journey(jS,jE);

        assertThat(journey.durationMinutes(), is("81:37"));
    }


}
