package com.tfl.biling;

import com.tfl.biling.utils.AdjustableClock;
import com.tfl.billing.Journey;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyStart;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class JourneyTest {

    private AdjustableClock controlClock;
    private JourneyStart journeyStart;
    private JourneyEnd journeyEnd;
    private Journey journey;

    @Before
    public void setVars() {
        controlClock = new AdjustableClock();

        controlClock.setCurrentTime(20, 0, 23);
        journeyStart = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(), controlClock);

        controlClock.setCurrentTime(21,22,0);
        journeyEnd = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(), controlClock);

        journey = new Journey(journeyStart, journeyEnd);
    }

    @Test
    public void durationSecondsTest(){
        controlClock.setCurrentTime(20, 0, 23);
        journeyStart = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(), controlClock);

        controlClock.setCurrentTime(22,22,0);
        journeyEnd = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(), controlClock);

        journey = new Journey(journeyStart, journeyEnd);
        assertThat(journey.durationSeconds(), is(8497));
    }

    @Test
    public void durationMinutesTest(){
        controlClock.setCurrentTime(20, 0, 23);
        journeyStart = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);

        controlClock.setCurrentTime(21,22,00);
        journeyEnd = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);

        journey = new Journey(journeyStart, journeyEnd);

        assertThat(journey.durationMinutes(), is("81:37"));
    }
}
