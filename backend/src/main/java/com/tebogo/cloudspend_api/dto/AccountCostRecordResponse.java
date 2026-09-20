package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.model.ResourceType;

public record AccountCostRecordResponse(
        Long id,
        Long resourceId,
        String resourceName,
        String providerResourceId,
        ResourceType resourceType,
        BigDecimal amount,
        CurrencyCode currency,
        LocalDate periodStart,
        LocalDate periodEnd,
        Instant createdAt
) {
}