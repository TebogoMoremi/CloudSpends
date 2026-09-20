package com.tebogo.cloudspend_api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.AccountCostRecordResponse;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.service.CostRecordService;

@RestController
@RequestMapping("/api/cloud-accounts/{accountId}/costs")
public class AccountCostRecordController {

    private final CostRecordService costRecordService;

    public AccountCostRecordController(
            CostRecordService costRecordService) {

        this.costRecordService = costRecordService;
    }

    @GetMapping("/records")
    public List<AccountCostRecordResponse> getCostRecords(
            @PathVariable Long accountId,
            @RequestParam CurrencyCode currency,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return costRecordService.getAccountCostRecords(
                accountId,
                currency,
                startDate,
                endDate
        );
    }
}