package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;

import com.tebogo.cloudspend_api.model.BudgetStatus;
import com.tebogo.cloudspend_api.model.CurrencyCode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBudgetRequest(

        @NotBlank
        String budgetName,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        CurrencyCode currency,

        @NotNull
        @Min(1)
        @Max(100)
        Integer alertThreshold,

        @NotNull
        BudgetStatus status
) {
}