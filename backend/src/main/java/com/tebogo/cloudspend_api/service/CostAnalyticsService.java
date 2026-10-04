package com.tebogo.cloudspend_api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tebogo.cloudspend_api.dto.CostTrendResponse;
import com.tebogo.cloudspend_api.dto.DailyCostResponse;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;
import com.tebogo.cloudspend_api.model.CostRecord;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CostRecordRepository;

@Service
public class CostAnalyticsService {

    private final CostRecordRepository costRecordRepository;
    private final CloudAccountRepository cloudAccountRepository;

    public CostAnalyticsService(
            CostRecordRepository costRecordRepository,
            CloudAccountRepository cloudAccountRepository) {

        this.costRecordRepository = costRecordRepository;
        this.cloudAccountRepository = cloudAccountRepository;
    }

    @Transactional(readOnly = true)
    public CostTrendResponse getCostTrend(
            Long accountId,
            CurrencyCode currency,
            LocalDate startDate,
            LocalDate endDate) {

        validateAccount(accountId);
        validateDateRange(startDate, endDate);

        List<CostRecord> records =
                costRecordRepository
                        .findByCloudResourceCloudAccountIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                                accountId,
                                currency,
                                endDate,
                                startDate
                        );

        Map<LocalDate, BigDecimal> dailyCostMap =
                createEmptyDailyMap(startDate, endDate);

        for (CostRecord record : records) {
            distributeCost(
                    record,
                    startDate,
                    endDate,
                    dailyCostMap
            );
        }

        List<DailyCostResponse> dailyCosts =
                new ArrayList<>();

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal highestDailyCost = BigDecimal.ZERO;
        LocalDate highestCostDate = null;

        for (Map.Entry<LocalDate, BigDecimal> entry
                : dailyCostMap.entrySet()) {

            BigDecimal dailyCost =
                    entry.getValue().setScale(
                            4,
                            RoundingMode.HALF_UP
                    );

            dailyCosts.add(
                    new DailyCostResponse(
                            entry.getKey(),
                            dailyCost
                    )
            );

            totalCost = totalCost.add(dailyCost);

            if (highestCostDate == null
                    || dailyCost.compareTo(
                            highestDailyCost) > 0) {

                highestDailyCost = dailyCost;
                highestCostDate = entry.getKey();
            }
        }

        totalCost = totalCost.setScale(
                4,
                RoundingMode.HALF_UP
        );

        long numberOfDays =
                ChronoUnit.DAYS.between(
                        startDate,
                        endDate
                ) + 1;

        BigDecimal averageDailyCost =
                totalCost.divide(
                        BigDecimal.valueOf(numberOfDays),
                        4,
                        RoundingMode.HALF_UP
                );

        return new CostTrendResponse(
                accountId,
                currency,
                startDate,
                endDate,
                totalCost,
                averageDailyCost,
                highestCostDate,
                highestDailyCost.setScale(
                        4,
                        RoundingMode.HALF_UP
                ),
                dailyCosts
        );
    }

    private Map<LocalDate, BigDecimal> createEmptyDailyMap(
            LocalDate startDate,
            LocalDate endDate) {

        Map<LocalDate, BigDecimal> dailyCostMap =
                new LinkedHashMap<>();

        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {

            dailyCostMap.put(
                    currentDate,
                    BigDecimal.ZERO
            );

            currentDate = currentDate.plusDays(1);
        }

        return dailyCostMap;
    }

    private void distributeCost(
            CostRecord record,
            LocalDate requestedStartDate,
            LocalDate requestedEndDate,
            Map<LocalDate, BigDecimal> dailyCostMap) {

        LocalDate recordStart =
                record.getPeriodStart();

        LocalDate recordEnd =
                record.getPeriodEnd();

        long recordDays =
                ChronoUnit.DAYS.between(
                        recordStart,
                        recordEnd
                ) + 1;

        if (recordDays <= 0) {
            return;
        }

        BigDecimal dailyAmount =
                record.getAmount()
                        .divide(
                                BigDecimal.valueOf(recordDays),
                                12,
                                RoundingMode.HALF_UP
                        );

        LocalDate overlapStart =
                recordStart.isAfter(requestedStartDate)
                        ? recordStart
                        : requestedStartDate;

        LocalDate overlapEnd =
                recordEnd.isBefore(requestedEndDate)
                        ? recordEnd
                        : requestedEndDate;

        if (overlapEnd.isBefore(overlapStart)) {
            return;
        }

        LocalDate currentDate = overlapStart;

        while (!currentDate.isAfter(overlapEnd)) {

            dailyCostMap.computeIfPresent(
                    currentDate,
                    (date, currentAmount) ->
                            currentAmount.add(dailyAmount)
            );

            currentDate = currentDate.plusDays(1);
        }
    }

    private void validateAccount(Long accountId) {

        if (!cloudAccountRepository.existsById(accountId)) {
            throw new CloudAccountNotFoundException(
                    accountId
            );
        }
    }

    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }
}