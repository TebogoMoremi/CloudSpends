package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tebogo.cloudspend_api.model.BudgetStatus;
import com.tebogo.cloudspend_api.model.CurrencyCode;

public record BudgetUtilizationResponse(
        Long budgetId,
        String budgetName,
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal utilizationPercentage,
        Integer alertThreshold,
        boolean thresholdReached,
        boolean overBudget,
        CurrencyCode currency,
        BudgetStatus status,
        LocalDate startDate,
        LocalDate endDate
) {
}