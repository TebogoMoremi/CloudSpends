package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tebogo.cloudspend_api.model.CurrencyCode;

public record CostTotalResponse(
        Long resourceId,
        CurrencyCode currency,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalCost
) {
}