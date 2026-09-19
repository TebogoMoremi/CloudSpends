package com.tebogo.cloudspend_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tebogo.cloudspend_api.dto.CreateCostRecordRequest;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;
import com.tebogo.cloudspend_api.exception.CloudResourceNotFoundException;
import com.tebogo.cloudspend_api.model.CloudResource;
import com.tebogo.cloudspend_api.model.CostRecord;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CloudResourceRepository;
import com.tebogo.cloudspend_api.repository.CostRecordRepository;

@Service
public class CostRecordService {

    private final CostRecordRepository costRecordRepository;
    private final CloudResourceRepository cloudResourceRepository;
    private final CloudAccountRepository cloudAccountRepository;

    public CostRecordService(
            CostRecordRepository costRecordRepository,
            CloudResourceRepository cloudResourceRepository,
            CloudAccountRepository cloudAccountRepository) {

        this.costRecordRepository = costRecordRepository;
        this.cloudResourceRepository = cloudResourceRepository;
        this.cloudAccountRepository = cloudAccountRepository;
    }

    public CostRecord createCost(
            Long resourceId,
            CreateCostRecordRequest request) {

        CloudResource resource = cloudResourceRepository
                .findById(resourceId)
                .orElseThrow(() ->
                        new CloudResourceNotFoundException(resourceId));

        if (request.periodEnd().isBefore(request.periodStart())) {
            throw new IllegalArgumentException(
                    "Period end cannot be before period start"
            );
        }

        CostRecord costRecord = new CostRecord(
                request.amount(),
                request.currency(),
                request.periodStart(),
                request.periodEnd(),
                resource
        );

        return costRecordRepository.save(costRecord);
    }

    public List<CostRecord> getCosts(Long resourceId) {

        if (!cloudResourceRepository.existsById(resourceId)) {
            throw new CloudResourceNotFoundException(resourceId);
        }

        return costRecordRepository.findByCloudResourceId(resourceId);
    }

    public BigDecimal calculateTotal(
            Long resourceId,
            CurrencyCode currency) {

        if (!cloudResourceRepository.existsById(resourceId)) {
            throw new CloudResourceNotFoundException(resourceId);
        }

        return costRecordRepository
                .findByCloudResourceIdAndCurrency(
                        resourceId,
                        currency
                )
                .stream()
                .map(CostRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotal(
            Long resourceId,
            CurrencyCode currency,
            LocalDate startDate,
            LocalDate endDate) {

        if (!cloudResourceRepository.existsById(resourceId)) {
            throw new CloudResourceNotFoundException(resourceId);
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        return costRecordRepository
                .findByCloudResourceIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                        resourceId,
                        currency,
                        endDate,
                        startDate
                )
                .stream()
                .map(CostRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateAccountTotal(
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

        return costRecordRepository
                .findByCloudResourceCloudAccountIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                        accountId,
                        currency,
                        endDate,
                        startDate
                )
                .stream()
                .map(CostRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}