package com.tebogo.cloudspend_api.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.CostTrendResponse;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.service.CostAnalyticsService;

@RestController
@RequestMapping("/api/cloud-accounts/{accountId}/analytics")
public class CostAnalyticsController {

    private final CostAnalyticsService costAnalyticsService;

    public CostAnalyticsController(
            CostAnalyticsService costAnalyticsService) {
        this.costAnalyticsService = costAnalyticsService;
    }

    @GetMapping("/cost-trend")
    public CostTrendResponse getCostTrend(
            @PathVariable Long accountId,
            @RequestParam CurrencyCode currency,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return costAnalyticsService.getCostTrend(
                accountId,
                currency,
                startDate,
                endDate
        );
    }
}