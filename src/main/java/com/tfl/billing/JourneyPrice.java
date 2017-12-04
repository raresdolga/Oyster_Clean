package com.tfl.billing;

import java.math.BigDecimal;

public enum JourneyPrice {
    PEAK_LONG(new BigDecimal(3.80)), PEAK_SHORT(new BigDecimal(2.90)),
    OFF_PEAK_LONG(new BigDecimal(2.70)), OFF_PEAK_SHORT(new BigDecimal(1.60));

    private final BigDecimal value;

    JourneyPrice(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }
}
