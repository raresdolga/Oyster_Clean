package com.tfl.billing;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Rares on 27.11.2017.
 */
public class JourneyTest {
    private class MockSystemClock implements Clock{

        private LocalTime time;

        public MockSystemClock(){

        }

        @Override
        public long now() {
            return time.getLong(ChronoField.EPOCH_DAY);
        }

        public void setCurrentTime(int hour, int min, int sec) {
            time = LocalTime.of(hour, min, sec);
        }
    }
    JourneyStart jS = new JourneyStart(UUID.randomUUID(), UUID.randomUUID());
    JourneyEnd jE = new JourneyEnd(UUID.randomUUID(), UUID.randomUUID());

    private Journey journey = new Journey(jS,jE);
    private  MockSystemClock  controlClock = new MockSystemClock();
    @Test
    public void durationSecondsTest(){

        controlClock.setCurrentTime(20,00, 23);
        int duration = (int) ((journey.endTime().getTime() - journey.startTime().getTime()) / 1000);
        int seconds = journey.durationSeconds();

        assertThat(seconds, is(duration));
    }

    @Test
    public void durationMinutesTest(){
        int duration = journey.durationSeconds();
        String dur = duration/60 + ":" +  duration%60;
        assertThat(journey.durationMinutes(), is(dur));
    }

    @Test
    public void formattedStartTimeTest(){
        String ret = journey.formattedStartTime();
        SimpleDateFormat SDF = new SimpleDateFormat();
        assertThat(ret, is(SDF.format(new Date(jS.time()))));
    }

    @Test
    public void formattedEndTimeTest(){
        String ret = journey.formattedEndTime();
        SimpleDateFormat SDF = new SimpleDateFormat();
        assertThat(ret, is(SDF.format(new Date(jE.time()))));
    }

}
