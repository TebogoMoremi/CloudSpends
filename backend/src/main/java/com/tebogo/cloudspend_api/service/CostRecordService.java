package com.tebogo.cloudspend_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tebogo.cloudspend_api.dto.AccountCostRecordResponse;
import com.tebogo.cloudspend_api.dto.CreateCostRecordRequest;
import com.tebogo.cloudspend_api.dto.ResourceTypeCostResponse;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;
import com.tebogo.cloudspend_api.exception.CloudResourceNotFoundException;
import com.tebogo.cloudspend_api.model.CloudResource;
import com.tebogo.cloudspend_api.model.CostRecord;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CloudResourceRepository;
import com.tebogo.cloudspend_api.repository.CostRecordRepository;
import org.springframework.transaction.annotation.Transactional;

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

    /*
     * Create a new cost record for a cloud resource.
     */
    public CostRecord createCost(
            Long resourceId,
            CreateCostRecordRequest request) {

        CloudResource resource = cloudResourceRepository
                .findById(resourceId)
                .orElseThrow(() ->
                        new CloudResourceNotFoundException(resourceId));

        validateDateRange(
                request.periodStart(),
                request.periodEnd()
        );

        CostRecord costRecord = new CostRecord(
                request.amount(),
                request.currency(),
                request.periodStart(),
                request.periodEnd(),
                resource
        );

        return costRecordRepository.save(costRecord);
    }

    /*
     * Get all cost records belonging to one resource.
     */
    public List<CostRecord> getCosts(Long resourceId) {

        validateResource(resourceId);

        return costRecordRepository
                .findByCloudResourceId(resourceId);
    }

    /*
     * Calculate total cost for a resource without
     * applying a date range.
     */
    public BigDecimal calculateTotal(
            Long resourceId,
            CurrencyCode currency) {

        validateResource(resourceId);

        return costRecordRepository
                .findByCloudResourceIdAndCurrency(
                        resourceId,
                        currency
                )
                .stream()
                .map(CostRecord::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    /*
     * Calculate total cost for a resource within
     * a specific date range.
     */
    public BigDecimal calculateTotal(
            Long resourceId,
            CurrencyCode currency,
            LocalDate startDate,
            LocalDate endDate) {

        validateResource(resourceId);
        validateDateRange(startDate, endDate);

        return costRecordRepository
                .findByCloudResourceIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                        resourceId,
                        currency,
                        endDate,
                        startDate
                )
                .stream()
                .map(CostRecord::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    /*
     * Calculate the total cost for an entire
     * cloud account.
     */
    public BigDecimal calculateAccountTotal(
            Long accountId,
            CurrencyCode currency,
            LocalDate startDate,
            LocalDate endDate) {

        validateAccount(accountId);
        validateDateRange(startDate, endDate);

        return costRecordRepository
                .findByCloudResourceCloudAccountIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                        accountId,
                        currency,
                        endDate,
                        startDate
                )
                .stream()
                .map(CostRecord::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    /*
     * Return aggregated costs grouped by
     * resource type.
     *
     * Example:
     *
     * EC2 -> 61.00
     * S3  -> 50.00
     */
    public List<ResourceTypeCostResponse> getCostBreakdownByResourceType(
            Long accountId,
            CurrencyCode currency,
            LocalDate startDate,
            LocalDate endDate) {

        validateAccount(accountId);
        validateDateRange(startDate, endDate);

        return costRecordRepository
                .findCostBreakdownByResourceType(
                        accountId,
                        currency,
                        startDate,
                        endDate
                )
                .stream()
                .map(result ->
                        new ResourceTypeCostResponse(
                                result.getResourceType(),
                                result.getCost()
                        )
                )
                .toList();
    }

    /*
     * Return the individual cost records belonging
     * to an entire cloud account.
     *
     * Unlike the resource-type breakdown, these
     * records are NOT aggregated.
     *
     * For example:
     *
     * EC2 -> 61.00
     * S3  -> 25.00
     * S3  -> 25.00
     *
     * The two S3 records remain separate.
     */
    @Transactional(readOnly = true)
public List<AccountCostRecordResponse> getAccountCostRecords(
            Long accountId,
            CurrencyCode currency,
            LocalDate startDate,
            LocalDate endDate) {

        validateAccount(accountId);
        validateDateRange(startDate, endDate);

        List<CostRecord> costRecords =
                costRecordRepository
                        .findByCloudResourceCloudAccountIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                                accountId,
                                currency,
                                endDate,
                                startDate
                        );

        return costRecords
                .stream()
                .map(this::mapToAccountCostRecordResponse)
                .toList();
    }

    /*
     * Convert CostRecord entity into the DTO
     * returned to the frontend.
     */
    private AccountCostRecordResponse mapToAccountCostRecordResponse(
            CostRecord costRecord) {

        CloudResource resource =
                costRecord.getCloudResource();

        return new AccountCostRecordResponse(
                costRecord.getId(),
                resource.getId(),
                resource.getResourceName(),
                resource.getResourceId(),
                resource.getResourceType(),
                costRecord.getAmount(),
                costRecord.getCurrency(),
                costRecord.getPeriodStart(),
                costRecord.getPeriodEnd(),
                costRecord.getCreatedAt()
        );
    }

    /*
     * Validate that the cloud account exists.
     */
    private void validateAccount(Long accountId) {

        if (!cloudAccountRepository.existsById(accountId)) {
            throw new CloudAccountNotFoundException(accountId);
        }
    }

    /*
     * Validate that the cloud resource exists.
     */
    private void validateResource(Long resourceId) {

        if (!cloudResourceRepository.existsById(resourceId)) {
            throw new CloudResourceNotFoundException(resourceId);
        }
    }

    /*
     * Validate start/end dates.
     */
    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }
    
}