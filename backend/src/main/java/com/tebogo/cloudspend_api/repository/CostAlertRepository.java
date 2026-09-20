package com.tebogo.cloudspend_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tebogo.cloudspend_api.model.AlertStatus;
import com.tebogo.cloudspend_api.model.AlertType;
import com.tebogo.cloudspend_api.model.CostAlert;

public interface CostAlertRepository
        extends JpaRepository<CostAlert, Long> {

    List<CostAlert> findByCloudAccountIdOrderByCreatedAtDesc(
            Long cloudAccountId
    );

    List<CostAlert> findByCloudAccountIdAndStatusOrderByCreatedAtDesc(
            Long cloudAccountId,
            AlertStatus status
    );

    boolean existsByBudgetIdAndAlertTypeAndStatus(
            Long budgetId,
            AlertType alertType,
            AlertStatus status
    );

    Optional<CostAlert> findByBudgetIdAndAlertTypeAndStatus(
            Long budgetId,
            AlertType alertType,
            AlertStatus status
    );
}