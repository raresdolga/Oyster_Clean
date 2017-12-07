package com.tfl.billing;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SystemClock implements Clock {
    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
