package com.tebogo.cloudspend_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tebogo.cloudspend_api.model.Budget;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    List<Budget> findByCloudAccountId(Long cloudAccountId);
}