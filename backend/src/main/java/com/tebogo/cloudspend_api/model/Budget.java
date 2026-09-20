package com.tebogo.cloudspend_api.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String budgetName;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode currency;

    @Column(nullable = false)
    private Integer alertThreshold;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloud_account_id", nullable = false)
    @JsonIgnore
    private CloudAccount cloudAccount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Budget() {
    }

    public Budget(
            String budgetName,
            BigDecimal amount,
            CurrencyCode currency,
            Integer alertThreshold,
            BudgetStatus status,
            CloudAccount cloudAccount) {

        this.budgetName = budgetName;
        this.amount = amount;
        this.currency = currency;
        this.alertThreshold = alertThreshold;
        this.status = status;
        this.cloudAccount = cloudAccount;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public Integer getAlertThreshold() {
        return alertThreshold;
    }

    public BudgetStatus getStatus() {
        return status;
    }

    public CloudAccount getCloudAccount() {
        return cloudAccount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}