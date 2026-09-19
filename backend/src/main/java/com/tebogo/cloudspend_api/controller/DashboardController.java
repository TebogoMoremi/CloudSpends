package com.tebogo.cloudspend_api.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.DashboardSummaryResponse;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.service.DashboardService;

@RestController
@RequestMapping("/api/cloud-accounts/{accountId}/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardSummaryResponse getDashboard(
            @PathVariable Long accountId,
            @RequestParam CurrencyCode currency,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return dashboardService.getSummary(
                accountId,
                currency,
                startDate,
                endDate
        );
    }
}