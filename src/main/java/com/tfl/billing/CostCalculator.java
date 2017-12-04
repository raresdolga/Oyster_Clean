package com.tfl.billing;

import java.math.BigDecimal;
import java.util.List;


public interface CostCalculator {
    BigDecimal calculateCustomerTotal(List<Journey> journeys);
}
