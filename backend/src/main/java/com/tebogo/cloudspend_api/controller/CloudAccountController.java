package com.tebogo.cloudspend_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.CreateCloudAccountRequest;
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
}