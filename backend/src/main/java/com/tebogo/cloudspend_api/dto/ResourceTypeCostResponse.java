package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;

import com.tebogo.cloudspend_api.model.ResourceType;

public record ResourceTypeCostResponse(
        ResourceType resourceType,
        BigDecimal cost
) {
}