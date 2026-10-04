package com.tebogo.cloudspend_api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tebogo.cloudspend_api.dto.BudgetResponse;
import com.tebogo.cloudspend_api.dto.BudgetUtilizationResponse;
import com.tebogo.cloudspend_api.dto.CreateBudgetRequest;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;
import com.tebogo.cloudspend_api.model.Budget;
import com.tebogo.cloudspend_api.model.BudgetStatus;
import com.tebogo.cloudspend_api.model.CloudAccount;
import com.tebogo.cloudspend_api.model.CostRecord;
import com.tebogo.cloudspend_api.repository.BudgetRepository;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CostRecordRepository;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CloudAccountRepository cloudAccountRepository;
    private final CostRecordRepository costRecordRepository;
    private final CostAlertService costAlertService;

    public BudgetService(
            BudgetRepository budgetRepository,
            CloudAccountRepository cloudAccountRepository,
            CostRecordRepository costRecordRepository,
            CostAlertService costAlertService) {

        this.budgetRepository = budgetRepository;
        this.cloudAccountRepository = cloudAccountRepository;
        this.costRecordRepository = costRecordRepository;
        this.costAlertService = costAlertService;
    }

    @Transactional
    public BudgetResponse createBudget(
            Long accountId,
            CreateBudgetRequest request) {

        CloudAccount account = cloudAccountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new CloudAccountNotFoundException(accountId));

        Budget budget = new Budget(
                request.budgetName(),
                request.amount(),
                request.currency(),
                request.alertThreshold(),
                request.status(),
                account
        );

        Budget savedBudget =
                budgetRepository.save(budget);

        return mapToResponse(savedBudget);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgets(
            Long accountId) {

        validateAccount(accountId);

        return budgetRepository
                .findByCloudAccountId(accountId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetUtilizationResponse getBudgetUtilization(
            Long accountId,
            Long budgetId,
            LocalDate startDate,
            LocalDate endDate) {

        validateAccount(accountId);
        validateDateRange(startDate, endDate);

        Budget budget = budgetRepository
                .findById(budgetId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Budget not found: " + budgetId
                        ));

        if (!budget.getCloudAccount()
                .getId()
                .equals(accountId)) {

            throw new IllegalArgumentException(
                    "Budget does not belong to cloud account: "
                            + accountId
            );
        }

        List<CostRecord> costRecords =
                costRecordRepository
                        .findByCloudResourceCloudAccountIdAndCurrencyAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                                accountId,
                                budget.getCurrency(),
                                endDate,
                                startDate
                        );

        BigDecimal spentAmount = costRecords
                .stream()
                .map(CostRecord::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal remainingAmount =
                budget.getAmount()
                        .subtract(spentAmount);

        BigDecimal utilizationPercentage;

        if (budget.getAmount()
                .compareTo(BigDecimal.ZERO) > 0) {

            utilizationPercentage =
                    spentAmount
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .divide(
                                    budget.getAmount(),
                                    2,
                                    RoundingMode.HALF_UP
                            );

        } else {
            utilizationPercentage =
                    BigDecimal.ZERO;
        }

        boolean thresholdReached =
                utilizationPercentage.compareTo(
                        BigDecimal.valueOf(
                                budget.getAlertThreshold()
                        )
                ) >= 0;

        boolean overBudget =
                spentAmount.compareTo(
                        budget.getAmount()
                ) > 0;

        return new BudgetUtilizationResponse(
                budget.getId(),
                budget.getBudgetName(),
                budget.getAmount(),
                spentAmount,
                remainingAmount,
                utilizationPercentage,
                budget.getAlertThreshold(),
                thresholdReached,
                overBudget,
                budget.getCurrency(),
                budget.getStatus(),
                startDate,
                endDate
        );
    }

    @Transactional
    public BudgetResponse updateBudgetStatus(
            Long accountId,
            Long budgetId,
            BudgetStatus status) {

        CloudAccount account = cloudAccountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new CloudAccountNotFoundException(accountId));

        Budget budget = budgetRepository
                .findById(budgetId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Budget not found: " + budgetId
                        ));

        if (!budget.getCloudAccount()
                .getId()
                .equals(account.getId())) {

            throw new IllegalArgumentException(
                    "Budget does not belong to cloud account: "
                            + accountId
            );
        }

        budget.setStatus(status);

        Budget savedBudget =
                budgetRepository.save(budget);

        /*
         * An inactive budget should no longer
         * have active monitoring alerts.
         *
         * We resolve the alerts instead of deleting
         * them so CloudSpend keeps its alert history.
         */
        if (status == BudgetStatus.INACTIVE) {

            costAlertService
                    .resolveOpenAlertsForBudget(
                            budgetId
                    );
        }

        return mapToResponse(savedBudget);
    }

    @Transactional
    public void deleteBudget(
            Long accountId,
            Long budgetId) {

        CloudAccount account = cloudAccountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new CloudAccountNotFoundException(accountId));

        Budget budget = budgetRepository
                .findById(budgetId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Budget not found: " + budgetId
                        ));

        if (!budget.getCloudAccount()
                .getId()
                .equals(account.getId())) {

            throw new IllegalArgumentException(
                    "Budget does not belong to cloud account: "
                            + accountId
            );
        }

        budgetRepository.delete(budget);
    }

    private BudgetResponse mapToResponse(
            Budget budget) {

        return new BudgetResponse(
                budget.getId(),
                budget.getBudgetName(),
                budget.getAmount(),
                budget.getCurrency(),
                budget.getAlertThreshold(),
                budget.getStatus(),
                budget.getCloudAccount().getId(),
                budget.getCreatedAt()
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
}