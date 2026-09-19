package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tebogo.cloudspend_api.model.CurrencyCode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateCostRecordRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Amount cannot be negative"
        )
        BigDecimal amount,

        @NotNull(message = "Currency is required")
        CurrencyCode currency,

        @NotNull(message = "Period start is required")
        LocalDate periodStart,

        @NotNull(message = "Period end is required")
        LocalDate periodEnd

) {
}