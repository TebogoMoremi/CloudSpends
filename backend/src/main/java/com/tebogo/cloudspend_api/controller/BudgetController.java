package com.tebogo.cloudspend_api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.BudgetResponse;
import com.tebogo.cloudspend_api.dto.BudgetUtilizationResponse;
import com.tebogo.cloudspend_api.dto.CreateBudgetRequest;
import com.tebogo.cloudspend_api.service.BudgetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(
        "/api/cloud-accounts/{accountId}/budgets"
)
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(
            BudgetService budgetService) {

        this.budgetService = budgetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse createBudget(
            @PathVariable Long accountId,
            @Valid
            @RequestBody CreateBudgetRequest request) {

        return budgetService.createBudget(
                accountId,
                request
        );
    }

    @GetMapping
    public List<BudgetResponse> getBudgets(
            @PathVariable Long accountId) {

        return budgetService.getBudgets(accountId);
    }

    @GetMapping("/{budgetId}/utilization")
    public BudgetUtilizationResponse getBudgetUtilization(
            @PathVariable Long accountId,
            @PathVariable Long budgetId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return budgetService.getBudgetUtilization(
                accountId,
                budgetId,
                startDate,
                endDate
        );
    }
}