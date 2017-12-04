package com.tfl.billing;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Rares on 02.12.2017.
 */
public interface CostCalculator {
    BigDecimal calculateCustomerTotal(List<Journey> journeys);
}
