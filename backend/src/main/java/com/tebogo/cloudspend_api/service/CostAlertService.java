package com.tebogo.cloudspend_api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tebogo.cloudspend_api.dto.CostAlertResponse;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;
import com.tebogo.cloudspend_api.model.AlertSeverity;
import com.tebogo.cloudspend_api.model.AlertStatus;
import com.tebogo.cloudspend_api.model.AlertType;
import com.tebogo.cloudspend_api.model.Budget;
import com.tebogo.cloudspend_api.model.BudgetStatus;
import com.tebogo.cloudspend_api.model.CloudAccount;
import com.tebogo.cloudspend_api.model.CostAlert;
import com.tebogo.cloudspend_api.model.CostRecord;
import com.tebogo.cloudspend_api.repository.BudgetRepository;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CostAlertRepository;
import com.tebogo.cloudspend_api.repository.CostRecordRepository;

@Service
public class CostAlertService {

    private final CostAlertRepository costAlertRepository;
    private final BudgetRepository budgetRepository;
    private final CloudAccountRepository cloudAccountRepository;
    private final CostRecordRepository costRecordRepository;

    public CostAlertService(
            CostAlertRepository costAlertRepository,
            BudgetRepository budgetRepository,
            CloudAccountRepository cloudAccountRepository,
            CostRecordRepository costRecordRepository) {

        this.costAlertRepository = costAlertRepository;
        this.budgetRepository = budgetRepository;
        this.cloudAccountRepository = cloudAccountRepository;
        this.costRecordRepository = costRecordRepository;
    }

    @Transactional
    public List<CostAlertResponse> evaluateBudgets(
            Long accountId,
            LocalDate startDate,
            LocalDate endDate) {

        validateDateRange(startDate, endDate);

        CloudAccount account = cloudAccountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new CloudAccountNotFoundException(accountId));

        List<Budget> budgets =
                budgetRepository.findByCloudAccountId(accountId);

        for (Budget budget : budgets) {

            if (budget.getStatus() != BudgetStatus.ACTIVE) {
                continue;
            }

            BigDecimal spentAmount =
                    calculateSpentAmount(
                            accountId,
                            budget,
                            startDate,
                            endDate
                    );

            BigDecimal utilization =
                    calculateUtilization(
                            spentAmount,
                            budget.getAmount()
                    );

            evaluateBudget(
                    budget,
                    account,
                    spentAmount,
                    utilization
            );
        }

        return getAlerts(accountId);
    }

    @Transactional(readOnly = true)
    public List<CostAlertResponse> getAlerts(
            Long accountId) {

        validateAccount(accountId);

        return costAlertRepository
                .findByCloudAccountIdOrderByCreatedAtDesc(
                        accountId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CostAlertResponse> getOpenAlerts(
            Long accountId) {

        validateAccount(accountId);

        return costAlertRepository
                .findByCloudAccountIdAndStatusOrderByCreatedAtDesc(
                        accountId,
                        AlertStatus.OPEN
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public CostAlertResponse resolveAlert(
            Long accountId,
            Long alertId) {

        validateAccount(accountId);

        CostAlert alert = costAlertRepository
                .findById(alertId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Alert not found: " + alertId
                        ));

        if (!alert.getCloudAccount()
                .getId()
                .equals(accountId)) {

            throw new IllegalArgumentException(
                    "Alert does not belong to cloud account: "
                            + accountId
            );
        }

        if (alert.getStatus() == AlertStatus.OPEN) {
            alert.resolve();
        }

        return mapToResponse(alert);
    }

    private void evaluateBudget(
            Budget budget,
            CloudAccount account,
            BigDecimal spentAmount,
            BigDecimal utilization) {

        /*
         * CRITICAL
         *
         * If spending is greater than the budget,
         * resolve any existing WARNING and create
         * a CRITICAL alert.
         */
        if (spentAmount.compareTo(
                budget.getAmount()) > 0) {

            resolveOpenAlert(
                    budget.getId(),
                    AlertType.BUDGET_THRESHOLD
            );

            createAlertIfNeeded(
                    budget,
                    account,
                    AlertType.BUDGET_EXCEEDED,
                    AlertSeverity.CRITICAL,
                    "Budget exceeded",
                    budget.getBudgetName()
                            + " has exceeded its budget. "
                            + "Current utilization is "
                            + utilization
                            + "%."
            );

            return;
        }

        /*
         * Budget is no longer over budget.
         *
         * If a CRITICAL alert was previously open,
         * resolve it.
         */
        resolveOpenAlert(
                budget.getId(),
                AlertType.BUDGET_EXCEEDED
        );

        BigDecimal threshold =
                BigDecimal.valueOf(
                        budget.getAlertThreshold()
                );

        /*
         * WARNING
         *
         * Spending has reached the configured
         * threshold but has not exceeded the budget.
         */
        if (utilization.compareTo(threshold) >= 0) {

            createAlertIfNeeded(
                    budget,
                    account,
                    AlertType.BUDGET_THRESHOLD,
                    AlertSeverity.WARNING,
                    "Budget threshold reached",
                    budget.getBudgetName()
                            + " has reached "
                            + utilization
                            + "% of its budget."
            );

            return;
        }

        /*
         * HEALTHY
         *
         * Utilization is below the warning threshold,
         * so resolve any existing WARNING.
         */
        resolveOpenAlert(
                budget.getId(),
                AlertType.BUDGET_THRESHOLD
        );
    }

    private void createAlertIfNeeded(
            Budget budget,
            CloudAccount account,
            AlertType alertType,
            AlertSeverity severity,
            String title,
            String message) {

        boolean alreadyExists =
                costAlertRepository
                        .existsByBudgetIdAndAlertTypeAndStatus(
                                budget.getId(),
                                alertType,
                                AlertStatus.OPEN
                        );

        if (alreadyExists) {
            return;
        }

        CostAlert alert = new CostAlert(
                alertType,
                severity,
                title,
                message,
                AlertStatus.OPEN,
                budget,
                account
        );

        costAlertRepository.save(alert);
    }

    private void resolveOpenAlert(
            Long budgetId,
            AlertType alertType) {

        costAlertRepository
                .findByBudgetIdAndAlertTypeAndStatus(
                        budgetId,
                        alertType,
                        AlertStatus.OPEN
                )
                .ifPresent(CostAlert::resolve);
    }

    private BigDecimal calculateSpentAmount(
            Long accountId,
            Budget budget,
            LocalDate startDate,
            LocalDate endDate) {

        List<CostRecord> records =
                costRecordRepository
                        .findByCloudResourceCloudAccountIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                                accountId,
                                budget.getCurrency(),
                                endDate,
                                startDate
                        );

        return records
                .stream()
                .map(CostRecord::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private BigDecimal calculateUtilization(
            BigDecimal spentAmount,
            BigDecimal budgetAmount) {

        if (budgetAmount.compareTo(
                BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO;
        }

        return spentAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        budgetAmount,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private CostAlertResponse mapToResponse(
            CostAlert alert) {

        return new CostAlertResponse(
                alert.getId(),
                alert.getAlertType(),
                alert.getSeverity(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getStatus(),
                alert.getBudget().getId(),
                alert.getBudget().getBudgetName(),
                alert.getCloudAccount().getId(),
                alert.getCreatedAt(),
                alert.getResolvedAt()
        );
    }

    private void validateAccount(
            Long accountId) {

        if (!cloudAccountRepository
                .existsById(accountId)) {

            throw new CloudAccountNotFoundException(
                    accountId
            );
        }
    }

    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        if (endDate.isBefore(startDate)) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }
    @Transactional
public void resolveOpenAlertsForBudget(Long budgetId) {

    resolveOpenAlert(
            budgetId,
            AlertType.BUDGET_THRESHOLD
    );

    resolveOpenAlert(
            budgetId,
            AlertType.BUDGET_EXCEEDED
    );
}
}