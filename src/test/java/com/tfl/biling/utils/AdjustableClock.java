package com.tfl.biling.utils;

import com.tfl.billing.Clock;

import java.time.LocalDateTime;
import java.time.ZoneId;

/*
    Clock with custom time used in mock testing to change system time
 */
public class AdjustableClock implements Clock {
    private LocalDateTime time;
    private long epoch = 0;
    private int day;
    private int month;
    private int year;

    public AdjustableClock(){
        LocalDateTime now = LocalDateTime.now();
        month = now.getMonthValue();
        day = now.getDayOfMonth();
        year = now.getYear();
    }

    @Override
    public long now() {
        return epoch;
    }

    public void setCurrentTime(int hour, int min, int sec) {
        // set just hour as system resets every night
        time = LocalDateTime.of(year, month, day, hour, min, sec);
        ZoneId zoneId = ZoneId.systemDefault();
        epoch = time.atZone(zoneId).toInstant().toEpochMilli();
    }

    public String getYearMonthDay() {
        StringBuilder str = new StringBuilder();
        str.append(day);
        str.append('.');
        str.append(month);
        str.append('.');
        str.append(year);
        return str.toString();
    }
}