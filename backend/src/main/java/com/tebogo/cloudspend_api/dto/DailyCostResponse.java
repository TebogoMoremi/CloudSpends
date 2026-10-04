package com.tebogo.cloudspend_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyCostResponse(
        LocalDate date,
        BigDecimal cost
) {
}