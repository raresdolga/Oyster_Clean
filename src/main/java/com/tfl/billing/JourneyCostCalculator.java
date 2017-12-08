package com.tfl.billing;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/*
    Strategy pattern used in TravelTracker

    Class responsible for computing the cost for a customer
 */
public class JourneyCostCalculator implements CostCalculator {
    private final BigDecimal PEAK_LONG = new BigDecimal(3.80);
    private final BigDecimal PEAK_SHORT = new BigDecimal(2.90);
    private final BigDecimal PEAK_CAP = new BigDecimal(9.00);
    private final BigDecimal OFF_PEAK_LONG = new BigDecimal(2.70);
    private final BigDecimal OFF_PEAK_SHORT = new BigDecimal(1.60);
    private final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.00);

    public JourneyCostCalculator() {

    }

    // compute cost of a client from his journeys
    public BigDecimal calculateCustomerTotal(List<Journey> customerJourneys) {
        BigDecimal customerTotal = new BigDecimal(0);

        boolean donePeak = containsPeak(customerJourneys);

        for (Journey journey : customerJourneys) {
            customerTotal = customerTotal.add(getJourneyPrice(journey));
        }

        if(donePeak && PEAK_CAP.compareTo(customerTotal) <= 0) {
            return roundToNearestPenny(PEAK_CAP);
        }

        if(!donePeak && OFF_PEAK_CAP.compareTo(customerTotal) <= 0) {
            return roundToNearestPenny(OFF_PEAK_CAP);
        }

        return roundToNearestPenny(customerTotal);
    }

    private BigDecimal getJourneyPrice(Journey journey) {
        BigDecimal journeyPrice;

        if(peak(journey) && isLongJourney(journey)) {
            journeyPrice = PEAK_LONG;
        } else if(peak(journey)) {
            journeyPrice = PEAK_SHORT;
        } else if(isLongJourney(journey)) {
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

    private boolean isLongJourney(Journey journey) {
        int minutesShortJourney = 25;
        return journey.durationSeconds() >= minutesShortJourney * 60;
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
