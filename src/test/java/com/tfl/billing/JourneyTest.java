package com.tfl.billing;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Rares on 27.11.2017.
 */
public class JourneyTest {
    /*
    * controllable clock  class just for testing
    */
    private class MockSystemClock implements Clock{
        private LocalDateTime time;
        private LocalDateTime now;
        private long epoch = 0;
        // need to set just the hour, min and seconds, we get the current day and month
        private int day;
        private int month;
        private int year;

        public MockSystemClock(){
            now = LocalDateTime.now();
            month = now.getMonthValue();
            day = now.getDayOfMonth();
            year = now.getYear();
        }

        @Override
        public long now() {
            return epoch;
        }

        public void setCurrentTime(int hour, int min, int sec) {
            // need to transform in in Epoch milliseconds so we use LocalDateTime, but we need to set just hour as system resets every night
            time = LocalDateTime.of(year,month,day,hour, min, sec);
            ZoneId zoneId = ZoneId.systemDefault();
            epoch = time.atZone(zoneId).toInstant().toEpochMilli();
        }
    }
    MockSystemClock controlClock;
    JourneyStart jS;
    JourneyEnd jE;
    private Journey journey;
    @Before
    public void setVars() {
        controlClock = new MockSystemClock();
        controlClock.setCurrentTime(20, 0, 23);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        controlClock.setCurrentTime(21,22,00);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
    }

    @Test
    public void durationSecondsTest(){
        controlClock.setCurrentTime(20, 0, 23);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        controlClock.setCurrentTime(22,22,00);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
        assertThat(journey.durationSeconds(), is(8497));
        // new values
        controlClock.setCurrentTime(10, 0, 0);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        controlClock.setCurrentTime(10,0,50);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
        assertThat(journey.durationSeconds(), is(50));
    }

    @Test
    public void durationMinutesTest(){
        controlClock.setCurrentTime(20, 0, 23);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        controlClock.setCurrentTime(21,22,00);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
        assertThat(journey.durationMinutes(), is("81:37"));
        // new values
        controlClock.setCurrentTime(0, 12, 0);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        controlClock.setCurrentTime(0,22,20);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
        assertThat(journey.durationMinutes(), is("10:20"));
    }

    @Test
    public void formattedStartTimeTest(){
        controlClock.setCurrentTime(17, 33, 23);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        controlClock.setCurrentTime(21,22,00);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
        assertThat(journey.formattedStartTime(), is("28.11.2017 17:33"));
        // new values
        controlClock.setCurrentTime(10,02,00);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
        assertThat(journey.formattedStartTime(), is("28.11.2017 10:02"));
    }

    @Test
    public void formattedEndTimeTest(){
        controlClock.setCurrentTime(15, 03, 23);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        controlClock.setCurrentTime(23,22,00);
        jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
        assertThat(journey.formattedStartTime(), is("28.11.2017 15:03"));
        // new values
        controlClock.setCurrentTime(22,02,00);
        jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID(),controlClock);
        journey = new Journey(jS,jE);
        assertThat(journey.formattedStartTime(), is("28.11.2017 22:02"));
    }

}
