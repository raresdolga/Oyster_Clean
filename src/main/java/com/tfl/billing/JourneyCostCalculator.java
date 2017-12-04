package com.tfl.billing;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JourneyCostCalculator implements CostCalculator{
    private final BigDecimal PEAK_LONG = new BigDecimal(3.80);
    private final BigDecimal PEAK_SHORT = new BigDecimal(2.90);
    private final BigDecimal PEAK_CAP = new BigDecimal(9.00);
    private final BigDecimal OFF_PEAK_LONG = new BigDecimal(2.70);
    private final BigDecimal OFF_PEAK_SHORT = new BigDecimal(1.60);
    private final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.00);

    public JourneyCostCalculator() {

    }

    public BigDecimal calculateCustomerTotal(List<Journey> journeys) {
        BigDecimal customerTotal = new BigDecimal(0);

        boolean donePeak = containsPeak(journeys);

        for (Journey journey : journeys)
            customerTotal = customerTotal.add(getProperJourneyPrice(journey));

        if(donePeak && PEAK_CAP.compareTo(customerTotal) <= 0)
            return roundToNearestPenny(PEAK_CAP);

        if(!donePeak && OFF_PEAK_CAP.compareTo(customerTotal) <= 0 )
            return roundToNearestPenny(OFF_PEAK_CAP);

        return roundToNearestPenny(customerTotal);
    }

    private BigDecimal getProperJourneyPrice(Journey journey) {
        BigDecimal journeyPrice;

        if(peak(journey) && isLongJorney(journey)) {
            journeyPrice = PEAK_LONG;
        } else if(peak(journey)) {
            journeyPrice = PEAK_SHORT;
        } else if(isLongJorney(journey)) {
            journeyPrice = OFF_PEAK_LONG;
        } else {
            journeyPrice = OFF_PEAK_SHORT;
        }

        return journeyPrice;
    }

    private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return isMorningPeakTime(hour) || isEveningPeakTime(hour);
    }

    private boolean isLongJorney(Journey journey){
        return journey.durationSeconds() >= 25 * 60;
    }

    private boolean isMorningPeakTime(int hour) {
        return hour >= 6 && hour <= 9;
    }

    private boolean isEveningPeakTime(int hour) {
        return hour >= 17 && hour <= 19;
    }

    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean containsPeak(List<Journey> journeys){
        for(Journey journey : journeys){
            if(peak(journey))
                return true;
        }
        return false;
    }
}
