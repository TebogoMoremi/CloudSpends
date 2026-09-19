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

import com.tebogo.cloudspend_api.dto.CostTotalResponse;
import com.tebogo.cloudspend_api.dto.CreateCostRecordRequest;
import com.tebogo.cloudspend_api.model.CostRecord;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.service.CostRecordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resources/{resourceId}/costs")
public class CostRecordController {

    private final CostRecordService costRecordService;

    public CostRecordController(
            CostRecordService costRecordService) {

        this.costRecordService = costRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CostRecord createCost(
            @PathVariable Long resourceId,
            @Valid @RequestBody CreateCostRecordRequest request) {

        return costRecordService.createCost(resourceId, request);
    }

    @GetMapping
    public List<CostRecord> getCosts(
            @PathVariable Long resourceId) {

        return costRecordService.getCosts(resourceId);
    }

    @GetMapping("/total")
    public CostTotalResponse getTotal(
            @PathVariable Long resourceId,
            @RequestParam CurrencyCode currency,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return new CostTotalResponse(
                resourceId,
                currency,
                startDate,
                endDate,
                costRecordService.calculateTotal(
                        resourceId,
                        currency,
                        startDate,
                        endDate
                )
        );
    }
}