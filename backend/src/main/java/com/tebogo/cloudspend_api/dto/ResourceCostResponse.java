package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;

import com.tebogo.cloudspend_api.model.ResourceStatus;
import com.tebogo.cloudspend_api.model.ResourceType;

public record ResourceCostResponse(
        Long id,
        String resourceName,
        String resourceId,
        ResourceType resourceType,
        String region,
        ResourceStatus status,
        BigDecimal cost
) {
}