package com.tebogo.cloudspend_api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.CostAlertResponse;
import com.tebogo.cloudspend_api.service.CostAlertService;

@RestController
@RequestMapping("/api/cloud-accounts/{accountId}/alerts")
public class CostAlertController {

    private final CostAlertService costAlertService;

    public CostAlertController(
            CostAlertService costAlertService) {

        this.costAlertService = costAlertService;
    }

    @GetMapping
    public List<CostAlertResponse> getAlerts(
            @PathVariable Long accountId) {

        return costAlertService.getAlerts(accountId);
    }

    @GetMapping("/open")
    public List<CostAlertResponse> getOpenAlerts(
            @PathVariable Long accountId) {

        return costAlertService.getOpenAlerts(accountId);
    }

    @PostMapping("/evaluate")
    public List<CostAlertResponse> evaluateBudgets(
            @PathVariable Long accountId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return costAlertService.evaluateBudgets(
                accountId,
                startDate,
                endDate
        );
    }

    @PatchMapping("/{alertId}/resolve")
    public CostAlertResponse resolveAlert(
            @PathVariable Long accountId,
            @PathVariable Long alertId) {

        return costAlertService.resolveAlert(
                accountId,
                alertId
        );
    }
}