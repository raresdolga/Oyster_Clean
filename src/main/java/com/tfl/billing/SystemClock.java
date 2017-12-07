package com.tfl.billing;

public class SystemClock implements Clock {
    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
