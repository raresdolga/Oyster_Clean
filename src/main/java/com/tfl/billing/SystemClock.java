package com.tfl.billing;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SystemClock implements Clock {
    @Override
    public long now() {
        LocalDateTime time =  LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        long epoch = time.atZone(zoneId).toEpochSecond();
        return  epoch;
    }
}
