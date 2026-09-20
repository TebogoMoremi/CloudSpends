package com.tebogo.cloudspend_api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.dto.CreateCloudResourceRequest;
import com.tebogo.cloudspend_api.dto.ResourceCostResponse;
import com.tebogo.cloudspend_api.model.CloudResource;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.service.CloudResourceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cloud-accounts/{cloudAccountId}/resources")
public class CloudResourceController {

    private final CloudResourceService cloudResourceService;

    public CloudResourceController(
            CloudResourceService cloudResourceService) {

        this.cloudResourceService = cloudResourceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CloudResource createResource(
            @PathVariable Long cloudAccountId,
            @Valid @RequestBody CreateCloudResourceRequest request) {

        return cloudResourceService.createResource(
                cloudAccountId,
                request
        );
    }

    @GetMapping
    public List<CloudResource> getResources(
            @PathVariable Long cloudAccountId) {

        return cloudResourceService
                .getResourcesByAccount(cloudAccountId);
    }

    @GetMapping("/with-costs")
    public List<ResourceCostResponse> getResourcesWithCosts(
            @PathVariable Long cloudAccountId,
            @RequestParam CurrencyCode currency,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return cloudResourceService.getResourcesWithCosts(
                cloudAccountId,
                currency,
                startDate,
                endDate
        );
    }
}