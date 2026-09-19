package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tebogo.cloudspend_api.model.CurrencyCode;

public record AccountCostTotalResponse(
        Long accountId,
        CurrencyCode currency,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalCost
) {
}