package com.tebogo.cloudspend_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tebogo.cloudspend_api.dto.CreateCloudResourceRequest;
import com.tebogo.cloudspend_api.dto.ResourceCostResponse;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;
import com.tebogo.cloudspend_api.model.CloudAccount;
import com.tebogo.cloudspend_api.model.CloudResource;
import com.tebogo.cloudspend_api.model.CurrencyCode;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CloudResourceRepository;
import com.tebogo.cloudspend_api.repository.CostRecordRepository;

@Service
public class CloudResourceService {

    private final CloudResourceRepository cloudResourceRepository;
    private final CloudAccountRepository cloudAccountRepository;
    private final CostRecordRepository costRecordRepository;

    public CloudResourceService(
            CloudResourceRepository cloudResourceRepository,
            CloudAccountRepository cloudAccountRepository,
            CostRecordRepository costRecordRepository) {

        this.cloudResourceRepository = cloudResourceRepository;
        this.cloudAccountRepository = cloudAccountRepository;
        this.costRecordRepository = costRecordRepository;
    }

    public CloudResource createResource(
            Long cloudAccountId,
            CreateCloudResourceRequest request) {

        CloudAccount account = cloudAccountRepository
                .findById(cloudAccountId)
                .orElseThrow(() ->
                        new CloudAccountNotFoundException(cloudAccountId));

        CloudResource resource = new CloudResource(
                request.resourceName(),
                request.resourceId(),
                request.resourceType(),
                request.region(),
                request.status(),
                account
        );

        return cloudResourceRepository.save(resource);
    }

    public List<CloudResource> getResourcesByAccount(
            Long cloudAccountId) {

        if (!cloudAccountRepository.existsById(cloudAccountId)) {
            throw new CloudAccountNotFoundException(cloudAccountId);
        }

        return cloudResourceRepository
                .findByCloudAccountId(cloudAccountId);
    }

    public List<ResourceCostResponse> getResourcesWithCosts(
            Long cloudAccountId,
            CurrencyCode currency,
            LocalDate startDate,
            LocalDate endDate) {

        if (!cloudAccountRepository.existsById(cloudAccountId)) {
            throw new CloudAccountNotFoundException(cloudAccountId);
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }

        List<CloudResource> resources =
                cloudResourceRepository
                        .findByCloudAccountId(cloudAccountId);

        return resources.stream()
                .map(resource -> {

                    BigDecimal cost =
                            costRecordRepository
                                    .sumCostByResourceAndPeriod(
                                            resource.getId(),
                                            currency,
                                            startDate,
                                            endDate
                                    );

                    return new ResourceCostResponse(
                            resource.getId(),
                            resource.getResourceName(),
                            resource.getResourceId(),
                            resource.getResourceType(),
                            resource.getRegion(),
                            resource.getStatus(),
                            cost
                    );
                })
                .toList();
    }
}