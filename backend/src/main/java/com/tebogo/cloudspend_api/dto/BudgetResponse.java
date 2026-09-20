package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.tebogo.cloudspend_api.model.BudgetStatus;
import com.tebogo.cloudspend_api.model.CurrencyCode;

public record BudgetResponse(
        Long id,
        String budgetName,
        BigDecimal amount,
        CurrencyCode currency,
        Integer alertThreshold,
        BudgetStatus status,
        Long cloudAccountId,
        Instant createdAt
) {
}