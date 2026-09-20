package com.tebogo.cloudspend_api.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tebogo.cloudspend_api.model.CostRecord;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.repository.projection.ResourceTypeCostProjection;

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

    @Query("""
        SELECT
            cr.cloudResource.resourceType AS resourceType,
            SUM(cr.amount) AS cost
        FROM CostRecord cr
        WHERE cr.cloudResource.cloudAccount.id = :accountId
          AND cr.currency = :currency
          AND cr.periodStart <= :endDate
          AND cr.periodEnd >= :startDate
        GROUP BY cr.cloudResource.resourceType
        ORDER BY SUM(cr.amount) DESC
        """)
    List<ResourceTypeCostProjection> findCostBreakdownByResourceType(
            @Param("accountId") Long accountId,
            @Param("currency") CurrencyCode currency,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT COALESCE(SUM(cr.amount), 0)
        FROM CostRecord cr
        WHERE cr.cloudResource.id = :resourceId
          AND cr.currency = :currency
          AND cr.periodStart <= :endDate
          AND cr.periodEnd >= :startDate
        """)
    BigDecimal sumCostByResourceAndPeriod(
            @Param("resourceId") Long resourceId,
            @Param("currency") CurrencyCode currency,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}