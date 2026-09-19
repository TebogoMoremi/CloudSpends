package com.tebogo.cloudspend_api.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "cloud_accounts")
public class CloudAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CloudProvider provider;

    @Column(unique = true)
    private String providerAccountId;

    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected CloudAccount() {
    }

    public CloudAccount(
            String accountName,
            CloudProvider provider,
            String providerAccountId,
            String region) {

        this.accountName = accountName;
        this.provider = provider;
        this.providerAccountId = providerAccountId;
        this.region = region;
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public CloudProvider getProvider() {
        return provider;
    }

    public String getProviderAccountId() {
        return providerAccountId;
    }

    public String getRegion() {
        return region;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setProvider(CloudProvider provider) {
        this.provider = provider;
    }

    public void setProviderAccountId(String providerAccountId) {
        this.providerAccountId = providerAccountId;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}