package com.tebogo.cloudspend_api.dto;

import java.time.Instant;

import com.tebogo.cloudspend_api.model.AlertSeverity;
import com.tebogo.cloudspend_api.model.AlertStatus;
import com.tebogo.cloudspend_api.model.AlertType;

public record CostAlertResponse(
        Long id,
        AlertType alertType,
        AlertSeverity severity,
        String title,
        String message,
        AlertStatus status,
        Long budgetId,
        String budgetName,
        Long cloudAccountId,
        Instant createdAt,
        Instant resolvedAt
) {
}