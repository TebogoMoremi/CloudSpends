package com.tebogo.cloudspend_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tebogo.cloudspend_api.model.CostRecord;
import com.tebogo.cloudspend_api.model.CurrencyCode;

public interface CostRecordRepository
        extends JpaRepository<CostRecord, Long> {

    List<CostRecord> findByCloudResourceId(
            Long cloudResourceId
    );

    List<CostRecord> findByCloudResourceIdAndCurrency(
            Long cloudResourceId,
            CurrencyCode currency
    );

    List<CostRecord>
            findByCloudResourceIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                    Long cloudResourceId,
                    CurrencyCode currency,
                    LocalDate endDate,
                    LocalDate startDate
            );

    List<CostRecord>
            findByCloudResourceCloudAccountIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                    Long cloudAccountId,
                    CurrencyCode currency,
                    LocalDate endDate,
                    LocalDate startDate
            );
}