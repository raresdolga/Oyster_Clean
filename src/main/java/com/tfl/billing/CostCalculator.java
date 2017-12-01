package com.tfl.billing;

import com.tfl.billing.Adaptors.PaymentSystemI;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class intended to calculate the total cost of a list of journeys.
 * Created by Ionut on 28.11.2017.
 */
public class CostCalculator {

    private final BigDecimal OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.40);
    private final BigDecimal PEAK_JOURNEY_PRICE = new BigDecimal(3.20);

    private PaymentSystemI paymentSystem;

    public CostCalculator(PaymentSystemI paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public void calculateCost(Customer customer, List<Journey> journeys) {
        BigDecimal customerTotal = calculateCustomerTotal(journeys);
        paymentSystem.charge(customer, journeys, roundToNearestPenny(customerTotal));
    }

    private BigDecimal calculateCustomerTotal(List<Journey> journeys) {
        BigDecimal customerTotal = new BigDecimal(0);

        for (Journey journey : journeys)
            customerTotal = customerTotal.add(getProperJourneyPrice(journey));

        return customerTotal;
    }

    private BigDecimal getProperJourneyPrice(Journey journey) {
        BigDecimal journeyPrice = peak(journey) ? PEAK_JOURNEY_PRICE : OFF_PEAK_JOURNEY_PRICE;

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

    private boolean isMorningPeakTime(int hour) {
        return hour >= 6 && hour <= 9;
    }

    private boolean isEveningPeakTime(int hour) {
        return hour >= 17 && hour <= 19;
    }

    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
