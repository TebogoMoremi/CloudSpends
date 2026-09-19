package com.tebogo.cloudspend_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tebogo.cloudspend_api.dto.DashboardSummaryResponse;
import com.tebogo.cloudspend_api.dto.ResourceTypeCostResponse;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.model.ResourceType;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CloudResourceRepository;

@Service
public class DashboardService {

    private final CloudAccountRepository cloudAccountRepository;
    private final CloudResourceRepository cloudResourceRepository;
    private final CostRecordService costRecordService;

    public DashboardService(
            CloudAccountRepository cloudAccountRepository,
            CloudResourceRepository cloudResourceRepository,
            CostRecordService costRecordService) {

        this.cloudAccountRepository = cloudAccountRepository;
        this.cloudResourceRepository = cloudResourceRepository;
        this.costRecordService = costRecordService;
    }

    public DashboardSummaryResponse getSummary(
            Long accountId,
            CurrencyCode currency,
            LocalDate startDate,
            LocalDate endDate) {

        if (!cloudAccountRepository.existsById(accountId)) {
            throw new CloudAccountNotFoundException(accountId);
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        BigDecimal totalCost =
                costRecordService.calculateAccountTotal(
                        accountId,
                        currency,
                        startDate,
                        endDate
                );

        List<ResourceTypeCostResponse> breakdown =
                costRecordService.getCostBreakdownByResourceType(
                        accountId,
                        currency,
                        startDate,
                        endDate
                );

        long resourceCount =
                cloudResourceRepository.countByCloudAccountId(accountId);

        ResourceType topResourceType = breakdown.isEmpty()
                ? null
                : breakdown.get(0).resourceType();

        return new DashboardSummaryResponse(
                accountId,
                currency,
                startDate,
                endDate,
                totalCost,
                resourceCount,
                topResourceType,
                breakdown
        );
    }
}