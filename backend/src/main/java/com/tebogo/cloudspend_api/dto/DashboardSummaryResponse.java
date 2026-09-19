package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.model.ResourceType;

public record DashboardSummaryResponse(
        Long accountId,
        CurrencyCode currency,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalCost,
        long resourceCount,
        ResourceType topResourceType,
        List<ResourceTypeCostResponse> breakdown
) {
}