package com.tfl.biling;

import com.tfl.biling.utils.AdjustableClock;
import com.tfl.billing.Journey;
import com.tfl.billing.JourneyCostCalculator;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyStart;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JourneyCostCalculatorTest {
    private final AdjustableClock clock = new AdjustableClock();

    private final JourneyCostCalculator journeyCostCalculator = new JourneyCostCalculator();
    private final UUID cardId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private final UUID reader_id1 = UUID.randomUUID();
    private final UUID reader_id2 = UUID.randomUUID();

    private Journey journey;
    private List<Journey> journeys = new ArrayList<>();
    private JourneyStart journeyStart;
    private JourneyEnd journeyEnd;

    @Before
    public void configure(){
        clock.setCurrentTime(8, 30, 0);
        journeyStart = new JourneyStart(cardId, reader_id1, clock);

        clock.setCurrentTime(9, 00, 0);
        journeyEnd = new JourneyEnd(cardId, reader_id2, clock);

        journey = new Journey(journeyStart, journeyEnd);
        journeys.add(journey);
    }

    @Test
    public void customerCostUnfinishedJourney() {
        List<Journey> journeys = new ArrayList<>();

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);
        assertThat(totalCost, is(new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void customerCostFinishedOffPeakJourney() {
        clock.setCurrentTime(9, 20, 0);
        JourneyStart journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(11, 20, 0);
        JourneyEnd journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        List<Journey> journeys = new ArrayList<>(Arrays.asList(new Journey(journeyStart, journeyEnd)));

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(3.80).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void dailyCapLongOffPeak() {
        journeys = new ArrayList<>();

        clock.setCurrentTime(10, 20, 0);
        JourneyStart journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(11, 20, 0);
        JourneyEnd journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        Journey journey = new Journey(journeyStart, journeyEnd);
        journeys.add(journey);
        journeys.add(journey);
        journeys.add(journey);

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(7.00).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void dailyCapLongPeak() {

        journeys.add(journey);
        journeys.add(journey);

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(9.00).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void dailyCapMixed() {

        clock.setCurrentTime(10, 30, 0);
        journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(11, 00, 0);
        journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        journey = new Journey(journeyStart,journeyEnd);
        journeys.add(journey);

        journeys.add(journey);
        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(9.00).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void dailyCapShortOffPeak() {
        journeys = new ArrayList<>();

        clock.setCurrentTime(10, 20, 0);
        JourneyStart journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(10, 35, 0);
        JourneyEnd journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        Journey journey = new Journey(journeyStart,journeyEnd);

        journeys.add(journey);
        journeys.add(journey);
        journeys.add(journey);
        journeys.add(journey);
        journeys.add(journey);

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(7.00).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void dailyCapShortPeak(){
        journeys = new ArrayList<>();

        clock.setCurrentTime(11, 20, 0);
        JourneyStart journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(11, 35, 0);
        JourneyEnd journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        Journey journey = new Journey(journeyStart,journeyEnd);
        journeys.add(journey);
        journeys.add(journey);

        clock.setCurrentTime(14, 40, 0);
        journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(15, 05, 0);
        journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        journey = new Journey(journeyStart,journeyEnd);
        journeys.add(journey);
        journeys.add(journey);
        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(7).setScale(2, BigDecimal.ROUND_HALF_UP)));

    }

    @Test
    public void shortOffPeak(){
        journeys = new ArrayList<>();

        clock.setCurrentTime(11, 20, 0);
        JourneyStart journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(11, 35, 0);
        JourneyEnd journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        Journey journey = new Journey(journeyStart,journeyEnd);
        journeys.add(journey);

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(1.60).setScale(2, BigDecimal.ROUND_HALF_UP)));

    }

    @Test
    public void shortPeak(){
        journeys = new ArrayList<>();

        clock.setCurrentTime(8, 20, 0);
        JourneyStart journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(8, 40, 0);
        JourneyEnd journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        Journey journey = new Journey(journeyStart,journeyEnd);
        journeys.add(journey);

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(2.90).setScale(2, BigDecimal.ROUND_HALF_UP)));

    }

    @Test
    public void longOffPeak(){
        journeys = new ArrayList<>();

        clock.setCurrentTime(22, 20, 0);
        JourneyStart journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(23, 35, 0);
        JourneyEnd journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        Journey journey = new Journey(journeyStart,journeyEnd);
        journeys.add(journey);

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(2.70).setScale(2, BigDecimal.ROUND_HALF_UP)));

    }

    @Test
    public void longPeak(){
        journeys = new ArrayList<>();

        clock.setCurrentTime(8, 20, 0);
        JourneyStart journeyStart = new JourneyStart(cardId,reader_id1, clock);

        clock.setCurrentTime(9, 35, 0);
        JourneyEnd journeyEnd = new JourneyEnd(cardId,reader_id2, clock);

        Journey journey = new Journey(journeyStart,journeyEnd);
        journeys.add(journey);

        BigDecimal totalCost = journeyCostCalculator.calculateCustomerTotal(journeys);

        assertThat(totalCost, is(new BigDecimal(3.80).setScale(2, BigDecimal.ROUND_HALF_UP)));

    }

}
