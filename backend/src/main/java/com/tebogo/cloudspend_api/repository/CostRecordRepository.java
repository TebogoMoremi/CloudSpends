package com.tebogo.cloudspend_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tebogo.cloudspend_api.model.CostRecord;

public interface CostRecordRepository
        extends JpaRepository<CostRecord, Long> {

    List<CostRecord> findByCloudResourceId(Long cloudResourceId);

    List<CostRecord> findByCloudResourceIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
            Long cloudResourceId,
            LocalDate periodStart,
            LocalDate periodEnd
    );
}