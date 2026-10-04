package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tebogo.cloudspend_api.model.CurrencyCode;

public record CostTrendResponse(
        Long accountId,
        CurrencyCode currency,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalCost,
        BigDecimal averageDailyCost,
        LocalDate highestCostDate,
        BigDecimal highestDailyCost,
        List<DailyCostResponse> dailyCosts
) {
}