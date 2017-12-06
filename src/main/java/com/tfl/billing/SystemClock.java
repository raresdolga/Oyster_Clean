package com.tfl.billing;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SystemClock implements Clock {
    private LocalDateTime now;
    private long epoch;

    @Override
    public long now() {
        now = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        //transform local time in milliseconds
        epoch = now.atZone(zoneId).toInstant().toEpochMilli();
        return  epoch;
    }
}
