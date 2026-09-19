package com.tebogo.cloudspend_api.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.AccountCostTotalResponse;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.service.CostRecordService;
import java.util.List;
import com.tebogo.cloudspend_api.dto.ResourceTypeCostResponse;

@RestController
@RequestMapping("/api/cloud-accounts/{accountId}/costs")
public class AccountCostController {

    private final CostRecordService costRecordService;

    public AccountCostController(
            CostRecordService costRecordService) {

        this.costRecordService = costRecordService;
    }

    @GetMapping("/total")
    public AccountCostTotalResponse getTotal(
            @PathVariable Long accountId,
            @RequestParam CurrencyCode currency,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return new AccountCostTotalResponse(
                accountId,
                currency,
                startDate,
                endDate,
                costRecordService.calculateAccountTotal(
                        accountId,
                        currency,
                        startDate,
                        endDate
                )
        );
    }
    @GetMapping("/breakdown")
public List<ResourceTypeCostResponse> getBreakdown(
        @PathVariable Long accountId,
        @RequestParam CurrencyCode currency,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate) {

    return costRecordService.getCostBreakdownByResourceType(
            accountId,
            currency,
            startDate,
            endDate
    );
}
}