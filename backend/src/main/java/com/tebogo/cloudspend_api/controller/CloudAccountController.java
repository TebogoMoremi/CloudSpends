package com.tebogo.cloudspend_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.CreateCloudAccountRequest;
import com.tebogo.cloudspend_api.dto.UpdateCloudAccountRequest;
import com.tebogo.cloudspend_api.model.CloudAccount;
import com.tebogo.cloudspend_api.service.CloudAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cloud-accounts")
public class CloudAccountController {

    private final CloudAccountService cloudAccountService;

    public CloudAccountController(
            CloudAccountService cloudAccountService) {
        this.cloudAccountService = cloudAccountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CloudAccount createAccount(
            @Valid @RequestBody CreateCloudAccountRequest request) {

        return cloudAccountService.createAccount(request);
    }

    @GetMapping
    public List<CloudAccount> getAllAccounts() {
        return cloudAccountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public CloudAccount getAccountById(
            @PathVariable Long id) {

        return cloudAccountService.getAccountById(id);
    }

    @PutMapping("/{id}")
    public CloudAccount updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCloudAccountRequest request) {

        return cloudAccountService.updateAccount(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            @PathVariable Long id) {

        cloudAccountService.deleteAccount(id);
    }
}